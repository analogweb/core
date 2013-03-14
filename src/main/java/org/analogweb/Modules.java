package org.analogweb;

import java.util.List;

/**
 * このアプリケーションを構成する、拡張可能なモジュールを管理します。<br/>
 * @author snowgoose
 */
public interface Modules extends Disposable {

    List<InvocationMetadataFactory> getInvocationMetadataFactories();

    Invoker getInvoker();

    ContainerAdaptor getInvocationInstanceProvider();

    List<InvocationProcessor> getInvocationProcessors();

    List<InvocationInterceptor> getInvocationInterceptors();

    InvocationFactory getInvocationFactory();

    ResponseResolver getResponseResolver();

    ResponseHandler getResponseHandler();

    ExceptionHandler getExceptionHandler();

    TypeMapperContext getTypeMapperContext();

    ContainerAdaptor getModulesContainerAdaptor();

    AttributesHandlers getAttributesHandlers();

    ResponseFormatter findResponseFormatter(Class<? extends Response> mapToResponse);
    
}
