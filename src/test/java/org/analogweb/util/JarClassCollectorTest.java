package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.analogweb.exception.AssertionFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class JarClassCollectorTest {

    private static final String PACKAGE_NAME = JarClassCollectorTest.class.getPackage().getName()
            + ".classcollector";
    private JarClassCollector collector;
    private ClassLoader classLoader;
    private ClassLoader smallClassLoader;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        collector = new JarClassCollector();
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        classLoader = null;
        smallClassLoader = null;
    }

    @Test
    public void testCollect() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(getClass().getCanonicalName().replace('.', '/') + ".jar");
        smallClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new URLClassLoader(new URL[] { resource });
            }
        });
        Collection<String> scannedClassNames = classNames(collector.collect("a.b.c", ResourceUtils
                .findPackageResources("a.b.c", smallClassLoader).get(0), smallClassLoader));
        assertThat(scannedClassNames.size(), is(2));
        assertThat(scannedClassNames.contains("a.b.c.Foo"), is(true));
        assertThat(scannedClassNames.contains("a.b.c.Foo.FooBaa"), is(true));

        scannedClassNames = classNames(collector.collect("a.b.d", ResourceUtils
                .findPackageResources("a.b.d", smallClassLoader).get(0), smallClassLoader));
        assertThat(scannedClassNames.size(), is(2));
        assertThat(scannedClassNames.contains("a.b.d.Baa"), is(true));
        assertThat(scannedClassNames.contains("a.b.d.BaaImpl"), is(true));

        scannedClassNames = classNames(collector.collect("a.b.e", ResourceUtils
                .findPackageResources("a.b.e", smallClassLoader).get(0), smallClassLoader));
        assertThat(scannedClassNames.size(), is(1));
        assertThat(scannedClassNames.contains("a.b.e.Baz"), is(true));
    }

    @Test
    public void testCollectAll() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(getClass().getCanonicalName().replace('.', '/') + ".jar");
        smallClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new URLClassLoader(new URL[] { resource });
            }
        });
        Collection<String> scannedClassNames = classNames(collector.collect(ResourceUtils
                .findPackageResources("a.b.c", smallClassLoader).get(0), smallClassLoader));
        assertThat(scannedClassNames.size(), is(5));
        assertThat(scannedClassNames.contains("a.b.c.Foo"), is(true));
        assertThat(scannedClassNames.contains("a.b.c.Foo.FooBaa"), is(true));

        assertThat(scannedClassNames.contains("a.b.d.Baa"), is(true));
        assertThat(scannedClassNames.contains("a.b.d.BaaImpl"), is(true));

        assertThat(scannedClassNames.contains("a.b.e.Baz"), is(true));
    }

    private Collection<String> classNames(Collection<Class<?>> classes) {
        List<String> result = new ArrayList<String>();
        for (Class<?> clazz : classes) {
            result.add(clazz.getCanonicalName());
        }
        return result;
    }

    @Test
    public void testCollectWithDirectory() throws Exception {
        Collection<Class<?>> collectedClasses = collector.collect(PACKAGE_NAME, ResourceUtils
                .findPackageResources(PACKAGE_NAME, classLoader).get(0), classLoader);
        assertThat(collectedClasses.isEmpty(), is(true));
    }

    @Test
    public void testCollectWithInvalidPackageName() throws Exception {
        Collection<Class<?>> collectedClasses = collector.collect("invalid.package.name", null,
                classLoader);
        assertThat(collectedClasses.isEmpty(), is(true));
    }

    @Test
    public void testCollectWithNullPackageName() throws Exception {
        final URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(getClass().getCanonicalName().replace('.', '/') + ".jar");
        smallClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new URLClassLoader(new URL[] { resource });
            }
        });
        Collection<Class<?>> result = collector.collect(null,
                ResourceUtils.findPackageResources("a.b.c", smallClassLoader).get(0),
                smallClassLoader);
        assertThat(result.size(), is(5));
    }

    @Test
    public void testCollectWithNullResourceURL() throws Exception {
        Collection<Class<?>> result = collector.collect("a.b.c", null, classLoader);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCollectWithNullClassLoader() throws Exception {
        thrown.expect(AssertionFailureException.class);
        collector.collect(PACKAGE_NAME, null, null);
    }

}
