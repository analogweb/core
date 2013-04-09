package org.analogweb.core;

import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvokerFactory;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.RequestValueResolver;
import org.analogweb.ResponseHandler;
import org.analogweb.ResponseResolver;
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
                .setResponseHandlerClass(ResponseHandler.class)
                .setResponseResolverClass(ResponseResolver.class)
                .setExceptionHandlerClass(ExceptionHandler.class)
                .setTypeMapperContextClass(TypeMapperContext.class)
                .addApplicationProcessorClass(ApplicationProcessor.class)
                .addInvocationMetadataFactoriesClass(InvocationMetadataFactory.class)
                .addAttributesHandlerClass(AttributesHandler.class)
                .addRequestValueResolverClass(RequestValueResolver.class);
    }

}
