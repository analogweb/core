package org.analogweb.core;

import org.analogweb.ApplicationContextResolver;
import org.analogweb.ContainerAdaptorFactory;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactory implements
        ContainerAdaptorFactory<SingletonInstanceContainerAdaptor> {

    @Override
    public SingletonInstanceContainerAdaptor createContainerAdaptor(
            ApplicationContextResolver resolver) {
        return new SingletonInstanceContainerAdaptor();
    }

}
