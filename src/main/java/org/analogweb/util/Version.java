package org.analogweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

public class Version {

    private static final Log log = Logs.getLog(Version.class);

    public static List<Version> load(ClassLoader classLoader) {
        try {
            Enumeration<URL> resources = classLoader.getResources("analogweb.version.properties");
            Properties properties = new Properties();
            while (resources.hasMoreElements()) {
                URL p = resources.nextElement();
                InputStream in = null;
                try {
                    in = p.openStream();
                    properties.load(in);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            if (properties.isEmpty()) {
                return Arrays.asList(loadViaPom(classLoader));
            } else {
                HashSet<String> artifactIds = new HashSet<String>();
                for (Object obj : properties.keySet()) {
                    String key = (String) obj;
                    if (StringUtils.isNotEmpty(key)) {
                        int index = key.indexOf('.');
                        if (index >= 0) {
                            String artifactId = key.substring(0, index);
                            if (artifactIds.contains(artifactId)
                                    || properties.containsKey(artifactId + ".version") == false) {
                                continue;
                            }
                            artifactIds.add(artifactId);
                        }
                    }
                }
                List<Version> versions = new LinkedList<Version>();
                for (String id : artifactIds) {
                    versions.add(new Version(id, properties.getProperty(id + ".version")));
                }
                return versions;
            }
        } catch (IOException e) {
            // ignore it.
            log.debug(e.getMessage());
        }
        log.log(Markers.BOOT_APPLICATION, "WB000001");
        return Collections.emptyList();
    }

    private static Version loadViaPom(ClassLoader classLoader) throws IOException {
        URL properties = classLoader
                .getResource("META-INF/org.analogweb/analogweb-core/pom.properties");
        if (properties == null) {
            return new Version("", "Unknown");
        }
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = properties.openStream();
            props.load(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return new Version("analogweb-core", props.getProperty("version"));
    }

    private String version;
    private String artifactId;

    Version(String artifactId, String version) {
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
