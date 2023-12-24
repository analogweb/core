package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Collection;

import org.analogweb.util.classcollector.Baa;
import org.analogweb.util.classcollector.Baz;
import org.analogweb.util.classcollector.Baz.Bee;
import org.analogweb.util.classcollector.Boo;
import org.analogweb.util.classcollector.Foo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class FileClassCollectorTest {

    private FileClassCollector collector;
    private static final String PACKAGE_NAME = FileClassCollectorTest.class.getPackage().getName() + ".classcollector";
    private ClassLoader classLoader;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        collector = new FileClassCollector();
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Test
    public void testCollect() throws Exception {
        Collection<Class<?>> collectedClasses = collector.collect(PACKAGE_NAME, getPackageURL(PACKAGE_NAME),
                classLoader);
        assertContainsInstanceOfTypes(collectedClasses,
                new Class<?>[] { Foo.class, Baa.class, Baz.class, Boo.class, Bee.class });
    }

    @Test
    public void testCollect2() throws Exception {
        // Unsupported.
        Collection<Class<?>> collectedClasses = collector.collect(getPackageURL(PACKAGE_NAME), classLoader);
        assertThat(collectedClasses.isEmpty(), is(true));
    }

    @Test
    public void testCollectWithNullPackageName() throws Exception {
        Collection<Class<?>> result = collector.collect(null, null, classLoader);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCollectWithNullResource() throws Exception {
        Collection<Class<?>> result = collector.collect(PACKAGE_NAME, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCollectWithInvalidPackageName() throws Exception {
        Collection<Class<?>> result = collector.collect("invalid.package.name", null, classLoader);
        assertTrue(result.isEmpty());
    }

    private void assertContainsInstanceOfTypes(Collection<?> actual, Class<?>... expectedClasses) {
        for (Class<?> expected : expectedClasses) {
            for (Object obj : actual) {
                if (expected.equals((obj instanceof Class<?>) ? obj : obj.getClass())) {
                    return;
                }
            }
            fail(String.format("actual collection not contains instance of class %s", expected));
        }
    }

    private URL getPackageURL(String packageName) {
        return ResourceUtils.findPackageResources(packageName, classLoader).get(0);
    }
}
