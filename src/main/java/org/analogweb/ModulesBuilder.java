package org.analogweb;

/**
 * アプリケーションにおける{@link Modules}を構成するビルダです。<br/>
 * {@link Modules}を構成するためのコンポーネント(型)の定義情報を保持します。
 * @author snowgoose
 */
public interface ModulesBuilder {

	Modules buildModules(ApplicationContextResolver resolver,
			ContainerAdaptor defaultContainer);

	ModulesBuilder setModulesProviderClass(
			Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass);

	ModulesBuilder setInvocationInstanceProviderClass(
			Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> invocationInstanceProviderClass);

	ModulesBuilder setInvocationFactoryClass(
			Class<? extends InvocationFactory> invocationFactoryClass);

	ModulesBuilder setInvokerFactoryClass(
			Class<? extends InvokerFactory> invokerFactoryClass);

    // TODO rename method #30
	ModulesBuilder setDirectionResolverClass(
			Class<? extends ResponseResolver> actionResultResolverClass);

	ModulesBuilder setExceptionHandlerClass(
			Class<? extends ExceptionHandler> exceptionHandlerClass);

    // TODO rename method #30
	ModulesBuilder setDirectionHandlerClass(
			Class<? extends ResponseHandler> actionResultHandlerClass);

	ModulesBuilder setTypeMapperContextClass(
			Class<? extends TypeMapperContext> typeMapperContextClass);

	ModulesBuilder addInvocationMetadataFactoriesClass(
			Class<? extends InvocationMetadataFactory> invocationMetadataFactoryClass);

	ModulesBuilder clearInvocationMetadataFactoriesClass();

	ModulesBuilder addInvocationProcessorClass(
			Class<? extends InvocationProcessor> invocationProcessorClass);

	ModulesBuilder addInvocationInterceptorClass(
			Class<? extends InvocationInterceptor> invocationInterceptorClass);

	ModulesBuilder clearInvocationProcessorClass();

	ModulesBuilder addAttributesHandlerClass(
			Class<? extends AttributesHandler> attributesHandlerClass);

	ModulesBuilder clearAttributesHanderClass();

    // TODO rename method #30
	ModulesBuilder addDirectionFormatterClass(
			Class<? extends Response> mapToDirectionClass,
			Class<? extends ResponseFormatter> directionFormatterClass);

	ModulesBuilder clearDirectionFormatterClass();

	ModulesBuilder ignore(Class<? extends MultiModule> multiModuleClass);

	ModulesBuilder filter(MultiModule.Filter multiModuleFilter);

}
