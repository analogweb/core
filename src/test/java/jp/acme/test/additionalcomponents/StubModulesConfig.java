package jp.acme.test.additionalcomponents;

import org.analogweb.ModulesBuilder;
import org.analogweb.UserModulesConfig;

/**
 * @author snowgoose
 */
public class StubModulesConfig implements UserModulesConfig {

    @Override
    public ModulesBuilder prepare(ModulesBuilder builder) {
        builder.addApplicationProcessorClass(StubPreProcessor.class);
        return builder;
    }
}
