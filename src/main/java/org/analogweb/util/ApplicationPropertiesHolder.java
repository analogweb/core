package org.analogweb.util;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.core.WebApplication;
import org.analogweb.exception.ApplicationConfigurationException;
import org.analogweb.exception.MissingRequiredParameterException;

/**
 * 唯一の{@link ApplicationProperties}のインスタンスを保持、管理します。
 * @author snowgoose
 */
public class ApplicationPropertiesHolder {

    private static ApplicationProperties properties;

    /**
     * {@link ApplicationProperties}を生成する為のコンポーネントです。
     * @author snowgoose
     */
    public static interface Creator {

        /**
         * 新しい{@link ApplicationProperties}のインスタンスを生成します。
         * @return {@link ApplicationProperties}
         */
        ApplicationProperties create();
    }

    /**
     * {@link Creator}の設定に従って、{@link ApplicationProperties}を生成、設定します。
     * @param app {@link Application}
     * @param config {@link Creator}
     * @return 生成された{@link ApplicationProperties}
     */
    public static ApplicationProperties configure(Application app, Creator config) {
        if (properties != null) {
            throw new ApplicationConfigurationException(
                    "re-configure ApplicationProperties not allowed.");
        }
        properties = config.create();
        return properties;
    }

    public static ApplicationProperties current() {
        if (properties == null) {
            throw new ApplicationConfigurationException("ApplicationProperties not configured yet.");
        }
        return properties;
    }

    public static void dispose(Application app) {
        properties = null;
    }

    public static class DefaultCreator implements Creator {

        private final Collection<String> packageNames;
        private final String applicationSpecifier;
        private final String tempDirectoryPath;

        public DefaultCreator(String packageNames, String applicationSpecifier,
                String tempDirectoryPath) {
            this.packageNames = createUserDefinedPackageNames(packageNames);
            this.applicationSpecifier = createApplicationSpecifier(applicationSpecifier);
            this.tempDirectoryPath = createTempDirPath(tempDirectoryPath);
        }

        @Override
        public ApplicationProperties create() {
            return new ApplicationProperties() {

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
            };
        }

        protected Set<String> createUserDefinedPackageNames(String tokenizedRootPackageNames) {
            if (StringUtils.isNotEmpty(tokenizedRootPackageNames)) {
                StringTokenizer tokenizer = new StringTokenizer(tokenizedRootPackageNames, ",");
                Set<String> packageNames = new HashSet<String>();
                while (tokenizer.hasMoreTokens()) {
                    packageNames.add(tokenizer.nextToken());
                }
                return Collections.unmodifiableSet(packageNames);
            } else {
                throw new MissingRequiredParameterException(
                        Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES);
            }
        }

        protected String createApplicationSpecifier(String specifier) {
            if (StringUtils.isEmpty(specifier)) {
                return StringUtils.EMPTY;
            } else {
                return specifier;
            }
        }

        protected String createTempDirPath(String tmpDirPath) {
            if (StringUtils.isEmpty(tmpDirPath)) {
                return System.getProperty("java.io.tmpdir") + "/"
                        + WebApplication.class.getCanonicalName();
            } else {
                return tmpDirPath + "/" + WebApplication.class.getCanonicalName();
            }
        }
    }
}
