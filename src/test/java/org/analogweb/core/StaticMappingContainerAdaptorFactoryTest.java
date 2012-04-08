package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;


import org.analogweb.core.StaticMappingContainerAdaptor;
import org.analogweb.core.StaticMappingContainerAdaptorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorFactoryTest {

    private StaticMappingContainerAdaptorFactory factory;

    private ServletContext context;

    @Before
    public void setUp() throws Exception {
        factory = new StaticMappingContainerAdaptorFactory();
        context = mock(ServletContext.class);
    }

    @Test
    public void testCreateContainerAdaptor() {
        StaticMappingContainerAdaptor adaptor = factory.createContainerAdaptor(context);
        StaticMappingContainerAdaptor adaptor2 = factory.createContainerAdaptor(context);
        assertSame(adaptor, adaptor2);
    }

}
