package org.analogweb.core;

import org.analogweb.Module;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;

/**
 * @author y2k2mt
 */
public abstract class AbstractModulesConfig implements ModulesConfig {

    @Override
    public boolean equals(Object other) {
        return other instanceof ModulesConfig
                && other.getClass().getCanonicalName().equals(getClass().getCanonicalName());
    }

    @Override
    public int hashCode() {
        return getClass().getCanonicalName().hashCode();
    }
}
