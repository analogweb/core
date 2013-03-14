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

    // TODO rename method #30
    ResponseResolver getDirectionResolver();

    // TODO rename method #30
    ResponseHandler getDirectionHandler();

    ExceptionHandler getExceptionHandler();

    TypeMapperContext getTypeMapperContext();

    ContainerAdaptor getModulesContainerAdaptor();

    AttributesHandlers getAttributesHandlers();

    // TODO rename method #30
    ResponseFormatter findDirectionFormatter(Class<? extends Response> mapToDirection);
    
}
