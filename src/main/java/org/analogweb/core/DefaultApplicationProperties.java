package org.analogweb.core;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;
import org.analogweb.util.SystemProperties;

/**
 * @author snowgooseyk
 */
public class DefaultApplicationProperties implements ApplicationProperties {

    private Map<String, Object> properties;

    public static DefaultApplicationProperties defaultProperties() {
        return properties(StringUtils.EMPTY);
    }

    public static DefaultApplicationProperties properties(String packageNames) {
        return properties(packageNames, null, null);
    }

    public static DefaultApplicationProperties properties(String packageNames,
            String tmpDirectoryPath, String defaultClientLocale) {
        Map<String, Object> properties = Maps.newHashMap(TEMP_DIR,
                (Object) createTempDirPath(tmpDirectoryPath));
        properties.put(LOCALE, createDefaultClientLocale(defaultClientLocale));
        properties.put(PACKAGES, createUserDefinedPackageNames(packageNames));
        return new DefaultApplicationProperties(properties);
    }

    public static DefaultApplicationProperties properties(Map<String, Object> properties) {
        return new DefaultApplicationProperties(properties);
    }

    protected DefaultApplicationProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public File getTempDir() {
        return (File) getProperties().get(TEMP_DIR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getComponentPackageNames() {
        return (Collection<String>) getProperties().get(PACKAGES);
    }

    @Override
    public Locale getDefaultClientLocale() {
        return (Locale) getProperties().get(LOCALE);
    }

    public String getStringProperty(String key) {
        return (String) getProperties().get(key);
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    };

    private static Locale createDefaultClientLocale(String locale) {
        if (StringUtils.isEmpty(locale)) {
            return Locale.getDefault();
        }
        List<String> values = StringUtils.split(locale.replace('_', '-'), '-');
        switch (values.size()) {
        case 2:
            return new Locale(values.get(0), values.get(1));
        case 3:
            return new Locale(values.get(0), values.get(1), values.get(2));
        default:
            return new Locale(values.get(0));
        }
    }

    private static Set<String> createUserDefinedPackageNames(String tokenizedRootPackageNames) {
        Set<String> packageNames = new HashSet<String>();
        if (StringUtils.isNotEmpty(tokenizedRootPackageNames)) {
            for (String packageName : StringUtils.split(tokenizedRootPackageNames, ',')) {
                packageNames.add(packageName);
            }
        }
        packageNames.add(Application.DEFAULT_PACKAGE_NAME);
        return Collections.unmodifiableSet(packageNames);
    }

    private static File createTempDirPath(String tmpDirPath) {
        String tmpDir = tmpDirPath;
        if (StringUtils.isEmpty(tmpDir)) {
            tmpDir = SystemProperties.tmpDir() + SystemProperties.fileSeparator()
                    + Application.class.getCanonicalName();
        }
        if (tmpDir.endsWith(SystemProperties.fileSeparator())) {
            tmpDir = tmpDir + Application.class.getCanonicalName();
        } else {
            tmpDir = tmpDir + SystemProperties.fileSeparator()
                    + Application.class.getCanonicalName();
        }
        String path = tmpDir + SystemProperties.fileSeparator() + UUID.randomUUID();
        while (new File(path).exists()) {
            path = tmpDir + SystemProperties.fileSeparator() + UUID.randomUUID();
        }
        return new File(path);
    }
}
