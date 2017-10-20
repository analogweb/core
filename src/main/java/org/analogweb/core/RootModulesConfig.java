package org.analogweb.core;

import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.InvokerFactory;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.RequestValueResolver;
import org.analogweb.ResponseHandler;
import org.analogweb.RenderableResolver;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.Assertion;

/**
 * default implementation of {@link ModulesConfig}
 * 
 * @author y2k2mt
 */
public final class RootModulesConfig extends AbstractModulesConfig {

	@Override
	public ModulesBuilder prepare(ModulesBuilder builder) {
		Assertion.notNull(builder, ModulesBuilder.class.getCanonicalName());
		return builder
				.setModulesProviderClass(
						StaticMappingContainerAdaptorFactory.class)
				.setInvocationInstanceProviderClass(
						SingletonInstanceContainerAdaptorFactory.class)
				.setInvocationFactoryClass(InvocationFactory.class)
				.setInvokerFactoryClass(InvokerFactory.class)
				.setResponseHandlerClass(ResponseHandler.class)
				.setRenderableResolverClass(RenderableResolver.class)
				.setExceptionHandlerClass(ExceptionHandler.class)
				.setTypeMapperContextClass(TypeMapperContext.class)
				.addApplicationProcessorClass(ApplicationProcessor.class)
				.addInvocationMetadataFactoriesClass(
						InvocationMetadataFactory.class)
				.addInvocationMetadataFinderClass(
						InvocationMetadataFinder.class)
				.addAttributesHandlerClass(AttributesHandler.class)
				.addRequestValueResolverClass(RequestValueResolver.class)
				.addExceptionMapperClass(ExceptionMapper.class);
	}
}
