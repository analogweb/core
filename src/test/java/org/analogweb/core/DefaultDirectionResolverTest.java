package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


import org.analogweb.Direction;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.DefaultDirectionResolver;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.core.direction.Text;
import org.junit.Before;
import org.junit.Test;

public class DefaultDirectionResolverTest extends DefaultDirectionResolver {
    
    private DefaultDirectionResolver resolver;
    private InvocationMetadata metadata;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        resolver = new DefaultDirectionResolver();
        metadata = mock(InvocationMetadata.class);
        context = mock(RequestContext.class);
    }

    @Test
    public void testResolve() {
        Direction invocationResult = mock(Direction.class);
        Direction actual = resolver.resolve(invocationResult, metadata, context);
        
        assertThat(actual,is(sameInstance(invocationResult)));
    }

    @Test
    public void testResolveWithNumber() {
        Integer invocationResult = 500;
        HttpStatus actual = (HttpStatus)resolver.resolve(invocationResult, metadata, context);
        
        assertThat(actual,is(sameInstance(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testResolveWithPrimitiveNumber() {
        int invocationResult = 500;
        HttpStatus actual = (HttpStatus)resolver.resolve(invocationResult, metadata, context);
        
        assertThat(actual,is(sameInstance(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testResolveWithText() {
        String invocationResult = "this is simple text.";
        Text actual = (Text)resolver.resolve(invocationResult, metadata, context);
        
        assertThat(actual.toString(),is(invocationResult));
    }

}
