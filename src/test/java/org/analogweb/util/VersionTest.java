package org.analogweb.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testLoadSingleProperties() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]) {

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                return Thread.currentThread().getContextClassLoader()
                        .getResources("org/analogweb/util/VersionTest-1.properties");
            }
        };
        List<Version> actual = Version.load(classLoader);
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getArtifactId(), is("analogweb-core"));
        assertThat(actual.get(0).getVersion(), is("1.0.0"));
    }

    @Test
    public void testLoadMultipleProperties() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]) {

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                return Collections.enumeration(Arrays.asList(
                        Thread.currentThread().getContextClassLoader()
                                .getResource("org/analogweb/util/VersionTest-1.properties"),
                        Thread.currentThread().getContextClassLoader()
                                .getResource("org/analogweb/util/VersionTest-2.properties")));
            }
        };
        List<Version> actual = Version.load(classLoader);
        assertThat(actual.size(), is(2));
        // Alphabetical order.
        assertThat(actual.get(0).getArtifactId(), is("analogweb-another-plugin"));
        assertThat(actual.get(0).getVersion(), is("1.0.1"));
    }

    @Test
    public void testLoadUnknownProperties() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]) {

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                return Collections.emptyEnumeration();
            }
        };
        List<Version> actual = Version.load(classLoader);
        assertThat(actual.size(), is(1));
        // Alphabetical order.
        assertThat(actual.get(0).getArtifactId(), is(""));
        assertThat(actual.get(0).getVersion(), is("Unknown"));
    }
}
