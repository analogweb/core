package org.analogweb.util;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.exception.ApplicationConfigurationException;

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

}
