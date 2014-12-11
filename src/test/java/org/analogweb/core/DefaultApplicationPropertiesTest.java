package org.analogweb.core;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.analogweb.Application;
import org.analogweb.util.SystemProperties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class DefaultApplicationPropertiesTest {

    private DefaultApplicationProperties properties;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testUsingDefaultCreator() throws IOException {
        properties = DefaultApplicationProperties.defaultProperties();
        Collection<String> actualPackageNames = properties.getComponentPackageNames();
        assertThat(actualPackageNames.size(), is(1));
        assertThat(actualPackageNames.contains(Application.class.getPackage().getName()), is(true));
        assertThat(properties.getTempDir().getPath(),
                startsWith(new File(SystemProperties.tmpDir() + SystemProperties.fileSeparator()
                        + Application.class.getCanonicalName()).getPath()));
    }

    @Test
    public void testUsingConfiguredDefaultCreator() throws IOException {
        File dir = folder.newFolder();
        String packageNames = "foo.baa,baz.boo";
        String locale = "en-us";
        String tempDirectoryPath = dir.getPath();
        properties = DefaultApplicationProperties.properties(packageNames, tempDirectoryPath,
                locale);
        Collection<String> actualPackageNames = properties.getComponentPackageNames();
        assertThat(actualPackageNames.size(), is(2));
        assertThat(actualPackageNames.containsAll(Arrays.asList("foo.baa", "baz.boo")), is(true));
        assertThat(properties.getTempDir().getPath(),
                startsWith(new File(dir.getPath() + SystemProperties.fileSeparator()
                        + Application.class.getCanonicalName()).getPath()));
        assertThat(properties.getDefaultClientLocale(), is(Locale.US));
    }

    @Test
    public void testUsingConfiguredDefaultCreatorWithEmptyPackageNames() throws IOException {
        File dir = folder.newFolder();
        String packageNames = "";
        String tempDirectoryPath = dir.getPath();
        String locale = "";
        properties = DefaultApplicationProperties.properties(packageNames, tempDirectoryPath,
                locale);
        assertThat(properties.getComponentPackageNames().isEmpty(), is(true));
        assertThat(properties.getDefaultClientLocale(), is(Locale.getDefault()));
    }
}
