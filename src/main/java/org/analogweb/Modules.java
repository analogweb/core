package org.analogweb;

import java.util.List;

/**
 * All modules that compose an application.
 * @author snowgoose
 */
public interface Modules extends Disposable,ResponseFormatterFinder {

    List<InvocationMetadataFactory> getInvocationMetadataFactories();

    List<InvocationMetadataFinder> getInvocationMetadataFinders();

    List<ExceptionMapper> getExceptionMappers();

    Invoker getInvoker();

    ContainerAdaptor getInvocationInstanceProvider();

    List<ApplicationProcessor> getApplicationProcessors();

    List<InvocationInterceptor> getInvocationInterceptors();

    InvocationFactory getInvocationFactory();

    RenderableResolver getResponseResolver();

    ResponseHandler getResponseHandler();

    ExceptionHandler getExceptionHandler();

    TypeMapperContext getTypeMapperContext();

    ContainerAdaptor getModulesContainerAdaptor();

    RequestValueResolvers getRequestValueResolvers();

    ResponseFormatter findResponseFormatter(Class<? extends Renderable> mapToResponse);
}
