package org.analogweb.core;

import org.analogweb.ApplicationContext;
import org.analogweb.ContainerAdaptorFactory;

/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorFactory implements ContainerAdaptorFactory<StaticMappingContainerAdaptor> {

    private static StaticMappingContainerAdaptor adaptor;

    @Override
    public StaticMappingContainerAdaptor createContainerAdaptor(ApplicationContext resolver) {
        if (adaptor == null) {
            adaptor = new StaticMappingContainerAdaptor();
        }
        return adaptor;
    }
}
