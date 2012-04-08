package org.analogweb.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;


import org.analogweb.core.SingletonInstanceContainerAdaptor;
import org.analogweb.core.SingletonInstanceContainerAdaptorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactoryTest {

    private ServletContext servletContext;

    @Before
    public void setUp() {
        servletContext = mock(ServletContext.class);
    }

    @Test
    public void testCreateContainerAdaptor() {
        SingletonInstanceContainerAdaptorFactory factory = new SingletonInstanceContainerAdaptorFactory();
        SingletonInstanceContainerAdaptor adaptor = factory.createContainerAdaptor(servletContext);
        assertNotNull(adaptor);
        SingletonInstanceContainerAdaptor otherAdaptor = factory
                .createContainerAdaptor(servletContext);
        assertNotSame(adaptor, otherAdaptor);
    }

}
