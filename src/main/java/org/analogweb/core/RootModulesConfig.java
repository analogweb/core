package org.analogweb.core;


import org.analogweb.AttributesHandler;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.RequestAttributesFactory;
import org.analogweb.RequestContextFactory;
import org.analogweb.ResultAttributesFactory;
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
                .setInvocationFactoryClass(InvocationFactory.class).setInvokerClass(Invoker.class)
                .setDirectionHandlerClass(DirectionHandler.class)
                .setDirectionResolverClass(DirectionResolver.class)
                .setExceptionHandlerClass(ExceptionHandler.class)
                .setTypeMapperContextClass(TypeMapperContext.class)
                .setRequestContextFactoryClass(RequestContextFactory.class)
                .setRequestAttributesFactoryClass(RequestAttributesFactory.class)
                .setResultAttributesFactoryClass(ResultAttributesFactory.class)
                .addInvocationProcessorClass(InvocationProcessor.class)
                .addInvocationMetadataFactoriesClass(InvocationMetadataFactory.class)
                .addAttributesHandlerClass(AttributesHandler.class);
    }

}
