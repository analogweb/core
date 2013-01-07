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

    InvocationFactory getInvocationFactory();

    DirectionResolver getDirectionResolver();

    DirectionHandler getDirectionHandler();

    ExceptionHandler getExceptionHandler();

    TypeMapperContext getTypeMapperContext();

    TypeMapper findTypeMapper(Class<? extends TypeMapper> clazz);

    ContainerAdaptor getOptionalContainerAdaptor();

    AttributesHandlers getAttributesHandlers();

    DirectionFormatter findDirectionFormatter(Class<? extends Direction> mapToDirection);
    
}
