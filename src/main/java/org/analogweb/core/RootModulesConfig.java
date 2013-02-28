package org.analogweb.core;

import org.analogweb.AttributesHandler;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationProcessor;
import org.analogweb.InvokerFactory;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.Assertion;

/**
 * default implementation of {@link ModulesConfig}
 * @author snowgoose
 */
public final class RootModulesConfig implements ModulesConfig {

    @Override
    public ModulesBuilder prepare(ModulesBuilder builder) {
        Assertion.notNull(builder, ModulesBuilder.class.getCanonicalName());
        return builder.setModulesProviderClass(StaticMappingContainerAdaptorFactory.class)
                .setInvocationInstanceProviderClass(SingletonInstanceContainerAdaptorFactory.class)
                .setInvocationFactoryClass(InvocationFactory.class).setInvokerFactoryClass(InvokerFactory.class)
                .setDirectionHandlerClass(DirectionHandler.class)
                .setDirectionResolverClass(DirectionResolver.class)
                .setExceptionHandlerClass(ExceptionHandler.class)
                .setTypeMapperContextClass(TypeMapperContext.class)
                .addInvocationProcessorClass(InvocationProcessor.class)
                .addInvocationMetadataFactoriesClass(InvocationMetadataFactory.class)
                .addAttributesHandlerClass(AttributesHandler.class);
    }

}
