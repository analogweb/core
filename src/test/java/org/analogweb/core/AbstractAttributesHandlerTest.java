package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AbstractAttributesHandlerTest {

    private AbstractAttributesHandler handler;
    private RequestContext requestContext;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        handler = new AbstractAttributesHandler() {
        };
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testNop() {
        assertThat(handler.resolveValue(requestContext, metadata, "foo", String.class, new Annotation[0]),
                is(nullValue()));
        handler.putAttributeValue(requestContext, "foo", new Object());
        handler.removeAttribute(requestContext, "foo");
    }
}
