package org.analogweb.core;

import javax.servlet.ServletContext;

import org.analogweb.ContainerAdaptorFactory;


/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactory implements
        ContainerAdaptorFactory<SingletonInstanceContainerAdaptor> {

    @Override
    public SingletonInstanceContainerAdaptor createContainerAdaptor(ServletContext servletContext) {
        return new SingletonInstanceContainerAdaptor();
    }

}
