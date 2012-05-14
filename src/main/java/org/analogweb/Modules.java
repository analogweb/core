package org.analogweb;

import java.util.List;
import java.util.Map;

/**
 * このアプリケーションを構成する、拡張可能なモジュールを管理します。<br/>
 * @author snowgoose
 */
public interface Modules extends Disposable {

    List<InvocationMetadataFactory> getInvocationMetadataFactories();

    Invoker getInvoker();

    ContainerAdaptor getInvocationInstanceProvider();

    List<InvocationProcessor> getInvocationProcessors();

    InvocationFactory getInvocationFactory();

    DirectionResolver getDirectionResolver();

    DirectionHandler getDirectionHandler();

    ExceptionHandler getExceptionHandler();

    TypeMapperContext getTypeMapperContext();

    TypeMapper findTypeMapper(Class<? extends TypeMapper> clazz);

    ContainerAdaptor getOptionalContainerAdaptor();

    RequestContextFactory getRequestContextFactory();

    RequestAttributesFactory getRequestAttributesFactory();

    List<AttributesHandler> getAttributesHandlers();

    ResultAttributesFactory getResultAttributesFactory();

    ResultAttributes getResultAttributes();

    Map<String, AttributesHandler> getAttributesHandlersMap();

    DirectionFormatter findDirectionFormatter(Class<? extends Direction> mapToDirection);

}
