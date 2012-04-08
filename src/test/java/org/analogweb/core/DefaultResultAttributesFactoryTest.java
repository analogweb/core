package org.analogweb.core;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.ResultAttributes;
import org.analogweb.core.DefaultResultAttributes;
import org.analogweb.core.DefaultResultAttributesFactory;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultResultAttributesFactoryTest {

    private DefaultResultAttributesFactory factory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        factory = new DefaultResultAttributesFactory();
    }

    /**
     * Test method for
     * {@link org.analogweb.core.DefaultResultAttributesFactory#createResultAttributes(java.util.Map)}
     * .
     */
    @Test
    public void testCreateResultAttributes() {
        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();

        ResultAttributes actual = factory.createResultAttributes(placers);
        ResultAttributes other = factory.createResultAttributes(placers);
        assertTrue(actual instanceof DefaultResultAttributes);
        assertTrue(other instanceof DefaultResultAttributes);
        assertNotSame(actual, other);
    }

}
