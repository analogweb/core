package org.analogweb;

/**
 * アプリケーションにおける{@link Modules}を構成するビルダです。<br/>
 * {@link Modules}を構成するためのコンポーネント(型)の定義情報を保持します。
 * 
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

	ModulesBuilder setDirectionResolverClass(
			Class<? extends DirectionResolver> actionResultResolverClass);

	ModulesBuilder setExceptionHandlerClass(
			Class<? extends ExceptionHandler> exceptionHandlerClass);

	ModulesBuilder setDirectionHandlerClass(
			Class<? extends DirectionHandler> actionResultHandlerClass);

	ModulesBuilder setTypeMapperContextClass(
			Class<? extends TypeMapperContext> typeMapperContextClass);

	ModulesBuilder addInvocationMetadataFactoriesClass(
			Class<? extends InvocationMetadataFactory> invocationMetadataFactoryClass);

	ModulesBuilder clearInvocationMetadataFactoriesClass();

	ModulesBuilder addInvocationProcessorClass(
			Class<? extends InvocationProcessor> invocationProcessorClass);

	ModulesBuilder clearInvocationProcessorClass();

	ModulesBuilder addAttributesHandlerClass(
			Class<? extends AttributesHandler> attributesHandlerClass);

	ModulesBuilder clearAttributesHanderClass();

	ModulesBuilder addDirectionFormatterClass(
			Class<? extends Direction> mapToDirectionClass,
			Class<? extends DirectionFormatter> directionFormatterClass);

	ModulesBuilder clearDirectionFormatterClass();

	ModulesBuilder ignore(Class<? extends MultiModule> multiModuleClass);

	ModulesBuilder filter(MultiModule.Filter multiModuleFilter);

}
