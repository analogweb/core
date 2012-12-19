package org.analogweb.core;

import org.analogweb.ApplicationContextResolver;
import org.analogweb.ContainerAdaptorFactory;

/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorFactory implements
        ContainerAdaptorFactory<StaticMappingContainerAdaptor> {

    private static StaticMappingContainerAdaptor adaptor;

    @Override
    public StaticMappingContainerAdaptor createContainerAdaptor(ApplicationContextResolver resolver) {
        if (adaptor == null) {
            adaptor = new StaticMappingContainerAdaptor();
        }
        return adaptor;
    }

}
