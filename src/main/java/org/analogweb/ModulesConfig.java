package org.analogweb;

/**
 * Configure {@link Modules} through the {@link ModulesBuilder}.
 * @author y2k2mt
 */
public interface ModulesConfig {

    /**
     * Compose and return {@link Modules}.
     * @param builder {@link ModulesBuilder}.
     * @return configured {@link ModulesBuilder}.
     */
    ModulesBuilder prepare(ModulesBuilder builder);
}
