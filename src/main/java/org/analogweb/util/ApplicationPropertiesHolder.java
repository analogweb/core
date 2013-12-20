package org.analogweb.util;

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
     * {@link Creator}の設定に従って、{@link ApplicationProperties}を生成、設定します。
     * @param app {@link Application}
     * @param config {@link ApplicationProperties}
     * @return {@link ApplicationProperties}
     */
    public static ApplicationProperties configure(Application app, ApplicationProperties config) {
        if (properties != null) {
            throw new ApplicationConfigurationException(
                    "re-configure ApplicationProperties not allowed.");
        }
        properties = config;
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
