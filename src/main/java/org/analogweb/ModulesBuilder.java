package org.analogweb;

/**
 * アプリケーションにおける{@link Modules}を構成するビルダです。<br/>
 * {@link Modules}を構成するためのコンポーネント(型)の定義情報を保持します。
 * @author snowgoose
 */
public interface ModulesBuilder {

    Modules buildModules(ApplicationContext resolver, ContainerAdaptor defaultContainer);

    ModulesBuilder setModulesProviderClass(
            Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass);

    ModulesBuilder setInvocationInstanceProviderClass(
            Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> invocationInstanceProviderClass);

    ModulesBuilder setInvocationFactoryClass(
            Class<? extends InvocationFactory> invocationFactoryClass);

    ModulesBuilder setInvokerFactoryClass(Class<? extends InvokerFactory> invokerFactoryClass);

    ModulesBuilder setResponseResolverClass(Class<? extends ResponseResolver> responseResolverClass);

    ModulesBuilder setExceptionHandlerClass(Class<? extends ExceptionHandler> exceptionHandlerClass);

    ModulesBuilder setResponseHandlerClass(Class<? extends ResponseHandler> responseHandlerClass);

    ModulesBuilder setTypeMapperContextClass(
            Class<? extends TypeMapperContext> typeMapperContextClass);

    ModulesBuilder addInvocationMetadataFactoriesClass(
            Class<? extends InvocationMetadataFactory> invocationMetadataFactoryClass);

    ModulesBuilder clearInvocationMetadataFactoriesClass();

    ModulesBuilder addInvocationMetadataFinderClass(
            Class<? extends InvocationMetadataFinder> invocationMetadataFinderClass);

    ModulesBuilder clearInvocationMetadataFinderClass();

    ModulesBuilder addApplicationProcessorClass(
            Class<? extends ApplicationProcessor> applicationProcessorClass);

    ModulesBuilder addInvocationInterceptorClass(
            Class<? extends InvocationInterceptor> invocationInterceptorClass);

    ModulesBuilder clearApplicationProcessorClass();

    ModulesBuilder addAttributesHandlerClass(
            Class<? extends AttributesHandler> attributesHandlerClass);

    ModulesBuilder clearAttributesHanderClass();

    ModulesBuilder addRequestValueResolverClass(
            Class<? extends RequestValueResolver> requestValueResolverClass);

    ModulesBuilder clearRequestValueResolverClass();

    ModulesBuilder addResponseFormatterClass(Class<? extends Renderable> mapToResponseClass,
            Class<? extends ResponseFormatter> responseFormatterClass);

    ModulesBuilder addExceptionMapperClass(Class<? extends ExceptionMapper> exceptionMapperClass);

    ModulesBuilder clearDirectionFormatterClass();

    ModulesBuilder ignore(Class<? extends MultiModule> multiModuleClass);

    ModulesBuilder filter(MultiModule.Filter multiModuleFilter);
}
