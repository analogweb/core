package org.analogweb.core;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.core.DefaultRequestAttributes;
import org.analogweb.core.DefaultRequestAttributesFactory;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

public class DefaultRequestAttributesFactoryTest {

    private DefaultRequestAttributesFactory factory;
    private Map<String, AttributesHandler> resolversMap;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        factory = new DefaultRequestAttributesFactory();
        resolversMap = Maps.newEmptyHashMap();
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testCreateWithMap() {
        RequestAttributes attributes = factory.createRequestAttributes(resolversMap,metadata);
        assertTrue(attributes instanceof DefaultRequestAttributes);
        RequestAttributes otherAttributes = factory.createRequestAttributes(resolversMap,metadata);
        assertNotSame(attributes, otherAttributes);
    }

}
