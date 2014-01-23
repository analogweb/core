package org.analogweb.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import org.analogweb.ApplicationContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactoryTest {

    private ApplicationContext resolver;

    @Before
    public void setUp() {
        resolver = mock(ApplicationContext.class);
    }

    @Test
    public void testCreateContainerAdaptor() {
        SingletonInstanceContainerAdaptorFactory factory = new SingletonInstanceContainerAdaptorFactory();
        SingletonInstanceContainerAdaptor adaptor = factory.createContainerAdaptor(resolver);
        assertNotNull(adaptor);
        SingletonInstanceContainerAdaptor otherAdaptor = factory.createContainerAdaptor(resolver);
        assertNotSame(adaptor, otherAdaptor);
    }
}
