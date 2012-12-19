package org.analogweb.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import org.analogweb.ApplicationContextResolver;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorFactoryTest {

    private ApplicationContextResolver resolver;

    @Before
    public void setUp() {
        resolver = mock(ApplicationContextResolver.class);
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
