package org.analogweb.core;

import org.analogweb.ApplicationContext;
import org.analogweb.ContainerAdaptorFactory;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactory implements
        ContainerAdaptorFactory<SingletonInstanceContainerAdaptor> {

    @Override
    public SingletonInstanceContainerAdaptor createContainerAdaptor(
            ApplicationContext resolver) {
        return new SingletonInstanceContainerAdaptor();
    }

}
