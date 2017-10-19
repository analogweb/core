package org.analogweb;

/**
 * Builder of {@link Modules}.
 * 
 * @author y2k2mt
 */
public interface ModulesBuilder {

	Modules buildModules(ApplicationContext resolver,
			ContainerAdaptor defaultContainer);

	ModulesBuilder setModulesProviderClass(
			Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass);

	ModulesBuilder setModulesProvider(
			ContainerAdaptorFactory<? extends ContainerAdaptor> modulesProvider);

	ModulesBuilder setInvocationInstanceProviderClass(
			Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> invocationInstanceProviderClass);

	ModulesBuilder setInvocationInstanceProvider(
			ContainerAdaptorFactory<? extends ContainerAdaptor> invocationInstanceProvider);

	ModulesBuilder setInvocationFactoryClass(
			Class<? extends InvocationFactory> invocationFactoryClass);

	ModulesBuilder setInvocationFactory(InvocationFactory invocationFactory);

	ModulesBuilder setInvokerFactoryClass(
			Class<? extends InvokerFactory> invokerFactoryClass);

	ModulesBuilder setInvokerFactory(InvokerFactory invokerFactory);

	ModulesBuilder setRenderableResolverClass(
			Class<? extends RenderableResolver> responseResolverClass);

	ModulesBuilder setRenderableResolver(RenderableResolver responseResolver);

	ModulesBuilder setExceptionHandlerClass(
			Class<? extends ExceptionHandler> exceptionHandlerClass);

	ModulesBuilder setExceptionHandler(ExceptionHandler exceptionHandler);

	ModulesBuilder setResponseHandlerClass(
			Class<? extends ResponseHandler> responseHandlerClass);

	ModulesBuilder setResponseHandler(ResponseHandler responseHandler);

	ModulesBuilder setTypeMapperContextClass(
			Class<? extends TypeMapperContext> typeMapperContextClass);

	ModulesBuilder setTypeMapperContext(TypeMapperContext typeMapperContext);

	ModulesBuilder addInvocationMetadataFactoriesClass(
			Class<? extends InvocationMetadataFactory> invocationMetadataFactoryClass);

	ModulesBuilder addInvocationMetadataFactories(
			InvocationMetadataFactory... invocationMetadataFactories);

	ModulesBuilder clearInvocationMetadataFactoriesClass();

	ModulesBuilder clearInvocationMetadataFactories();

	ModulesBuilder addInvocationMetadataFinderClass(
			Class<? extends InvocationMetadataFinder> invocationMetadataFinderClass);

	ModulesBuilder addInvocationMetadataFinder(
			InvocationMetadataFinder... invocationMetadataFinder);

	ModulesBuilder clearInvocationMetadataFinderClass();

	ModulesBuilder clearInvocationMetadataFinder();

	ModulesBuilder addApplicationProcessorClass(
			Class<? extends ApplicationProcessor> applicationProcessorClass);

	ModulesBuilder addApplicationProcessor(
			ApplicationProcessor... applicationProcessors);

	ModulesBuilder clearApplicationProcessorClass();

	ModulesBuilder clearApplicationProcessors();

	ModulesBuilder addInvocationInterceptorClass(
			Class<? extends InvocationInterceptor> invocationInterceptorClass);

	ModulesBuilder addInvocationInterceptor(
			InvocationInterceptor... invocationInterceptors);

	ModulesBuilder clearInvocationInterceptorClass();

	ModulesBuilder clearInvocationInterceptors();

	ModulesBuilder addAttributesHandlerClass(
			Class<? extends AttributesHandler> attributesHandlerClass);

	ModulesBuilder addAttributesHandler(AttributesHandler... attributesHandler);

	ModulesBuilder clearAttributesHanderClass();

	ModulesBuilder clearAttributesHanders();

	ModulesBuilder addRequestValueResolverClass(
			Class<? extends RequestValueResolver> requestValueResolverClass);

	ModulesBuilder addRequestValueResolver(
			RequestValueResolver... requestValueResolver);

	ModulesBuilder clearRequestValueResolverClass();

	ModulesBuilder clearRequestValueResolvers();

	ModulesBuilder addResponseFormatterClass(
			Class<? extends Renderable> mapToResponseClass,
			Class<? extends ResponseFormatter> responseFormatterClass);

	ModulesBuilder addResponseFormatters(
			Class<? extends Renderable> mapToResponseClass,
			ResponseFormatter... responseFormatters);

	ModulesBuilder addExceptionMapperClass(
			Class<? extends ExceptionMapper> exceptionMapperClass);

	ModulesBuilder addExceptionMapper(ExceptionMapper... exceptionMappers);

	ModulesBuilder clearDirectionFormatterClass();

	ModulesBuilder ignore(Class<? extends MultiModule> multiModuleClass);

	ModulesBuilder filter(MultiModule.Filter multiModuleFilter);
}
