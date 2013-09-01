package org.analogweb.core;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.util.StringUtils;
import org.analogweb.util.SystemProperties;

/**
 * @author snowgooseyk
 */
public class DefaultApplicationProperties implements ApplicationProperties {

    private final Collection<String> packageNames;
    private final String applicationSpecifier;
    private final String tempDirectoryPath;
    private final Locale defaultClientLocale;

    public static DefaultApplicationProperties defaultProperties() {
        return new DefaultApplicationProperties();
    }

    public static DefaultApplicationProperties properties(String packageNames) {
        return new DefaultApplicationProperties(packageNames, null, null, null);
    }

    public static DefaultApplicationProperties properties(String packageNames,
            String tmpDirectoryPath, String defaultClientLocale) {
        return new DefaultApplicationProperties(packageNames, null, tmpDirectoryPath,
                defaultClientLocale);
    }

    protected DefaultApplicationProperties() {
        this(Application.class.getPackage().getName(), null, null, null);
    }

    protected DefaultApplicationProperties(String packageNames, String applicationSpecifier,
            String tempDirectoryPath, String defaultClientLocale) {
        this.packageNames = createUserDefinedPackageNames(packageNames);
        this.applicationSpecifier = createApplicationSpecifier(applicationSpecifier);
        this.tempDirectoryPath = createTempDirPath(tempDirectoryPath);
        this.defaultClientLocale = createDefaultClientLocale(defaultClientLocale);
    }

    @Override
    public File getTempDir() {
        return new File(tempDirectoryPath);
    }

    @Override
    public Collection<String> getComponentPackageNames() {
        return packageNames;
    }

    @Override
    public String getApplicationSpecifier() {
        return applicationSpecifier;
    }

    @Override
    public Locale getDefaultClientLocale() {
        return defaultClientLocale;
    }

    protected Locale createDefaultClientLocale(String locale) {
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

    protected Set<String> createUserDefinedPackageNames(String tokenizedRootPackageNames) {
        Set<String> packageNames = new HashSet<String>();
        if (StringUtils.isNotEmpty(tokenizedRootPackageNames)) {
            for (String packageName : StringUtils.split(tokenizedRootPackageNames, ',')) {
                packageNames.add(packageName);
            }
        }
        return Collections.unmodifiableSet(packageNames);
    }

    protected String createApplicationSpecifier(String specifier) {
        if (StringUtils.isEmpty(specifier)) {
            return StringUtils.EMPTY;
        } else {
            return specifier;
        }
    }

    protected String createTempDirPath(String tmpDirPath) {
        String tmpDir = tmpDirPath;
        if (StringUtils.isEmpty(tmpDir)) {
            tmpDir = SystemProperties.tmpDir();
        }
        if (tmpDir.endsWith(SystemProperties.fileSeparator())) {
            return tmpDir + Application.class.getCanonicalName();
        }
        return tmpDir + SystemProperties.fileSeparator() + Application.class.getCanonicalName();
    }
}
