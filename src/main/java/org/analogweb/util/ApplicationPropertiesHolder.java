package org.analogweb.util;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.core.ApplicationConfigurationException;

/**
 * 唯一の{@link ApplicationProperties}のインスタンスを保持、管理します。
 * @author snowgoose
 */
public final class ApplicationPropertiesHolder {

    private ApplicationPropertiesHolder() {
        // nop.
    }

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
        private final Locale defaultClientLocale;

        public DefaultCreator() {
            this(Application.class.getPackage().getName(), null, null, null);
        }

        public DefaultCreator(String packageNames, String applicationSpecifier,
                String tempDirectoryPath,String defaultClientLocale) {
            this.packageNames = createUserDefinedPackageNames(packageNames);
            this.applicationSpecifier = createApplicationSpecifier(applicationSpecifier);
            this.tempDirectoryPath = createTempDirPath(tempDirectoryPath);
            this.defaultClientLocale = createDefaultClientLocale(defaultClientLocale);
        }

		protected Locale createDefaultClientLocale(String locale) {
			if (StringUtils.isEmpty(locale)) {
				return Locale.getDefault();
			}
			List<String> values = StringUtils.split(locale.replace('_', '-'),
					'-');
			switch (values.size()) {
			case 2:
				return new Locale(values.get(0), values.get(1));
			case 3:
				return new Locale(values.get(0), values.get(1), values.get(2));
			default:
				return new Locale(values.get(0));
			}
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

				@Override
				public Locale getDefaultClientLocale() {
					return defaultClientLocale;
				}
            };
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
}
