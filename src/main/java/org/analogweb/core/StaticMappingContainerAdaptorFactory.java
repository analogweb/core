package org.analogweb.core;

import javax.servlet.ServletContext;

import org.analogweb.ContainerAdaptorFactory;


/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorFactory implements
        ContainerAdaptorFactory<StaticMappingContainerAdaptor> {

    private static StaticMappingContainerAdaptor adaptor;

    @Override
    public StaticMappingContainerAdaptor createContainerAdaptor(ServletContext servletContext) {
        if (adaptor == null) {
            adaptor = new StaticMappingContainerAdaptor();
        }
        return adaptor;
    }

}
