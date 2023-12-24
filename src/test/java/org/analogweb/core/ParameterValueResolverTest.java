package org.analogweb.core;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.InvocationMetadata;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ParameterValueResolverTest {

    private ParameterValueResolver resolver;
    private RequestContext requestContext;
    private InvocationMetadata metadata;
    private Parameters params;
    private Parameters matrixParams;

    @Before
    public void setUp() throws Exception {
        resolver = new ParameterValueResolver();
        requestContext = mock(RequestContext.class);
        metadata = mock(InvocationMetadata.class);
        params = mock(Parameters.class);
        matrixParams = mock(Parameters.class);
    }

    @Test
	public void testResolveAttributeValue() {
		when(requestContext.getQueryParameters()).thenReturn(params);
		when(params.getValues("foo")).thenReturn(Arrays.asList("baa"));
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				String.class, null);
		assertThat(actual.toString(), is("baa"));
	}

    @Test
    public void testResolveAttributeValueViaFormParameters() {
        Parameters empty = mock(Parameters.class);
        when(requestContext.getQueryParameters()).thenReturn(empty);
        when(requestContext.getMatrixParameters()).thenReturn(empty);
        when(requestContext.getFormParameters()).thenReturn(params);
        when(requestContext.getRequestMethod()).thenReturn("POST");
        when(params.getValues("foo")).thenReturn(Arrays.asList("baa"));
        Object actual = resolver.resolveValue(requestContext, metadata, "foo", String.class, null);
        assertThat(actual.toString(), is("baa"));
    }

    @Test
    public void testNotResolveAttributeValueViaFormParameters() {
        Parameters empty = mock(Parameters.class);
        when(requestContext.getQueryParameters()).thenReturn(empty);
        when(requestContext.getMatrixParameters()).thenReturn(empty);
        when(requestContext.getFormParameters()).thenReturn(params);
        when(requestContext.getRequestMethod()).thenReturn("GET");
        when(params.getValues("foo")).thenReturn(Arrays.asList("baa"));
        Object actual = resolver.resolveValue(requestContext, metadata, "foo", String.class, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testResolveAttributeValueWithNullName() {
        Object actual = resolver.resolveValue(requestContext, metadata, null, String[].class, null);
        assertThat(actual, is(nullValue()));
    }

    @Test
	public void testResolveAttributeValueOfParameterArray() {
		when(requestContext.getQueryParameters()).thenReturn(params);
		when(params.getValues("foo")).thenReturn(Arrays.asList("baa", "baz"));
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				String[].class, null);
		String[] actualArray = (String[]) actual;
		assertThat(actualArray[0], is("baa"));
		assertThat(actualArray[1], is("baz"));
	}

    @Test
	public void testResolveAttributeNoParameterValue() {
		when(requestContext.getQueryParameters()).thenReturn(params);
		when(requestContext.getMatrixParameters()).thenReturn(params);
		when(requestContext.getFormParameters()).thenReturn(params);
		when(requestContext.getRequestMethod()).thenReturn("POST");
		when(params.getValues("foo")).thenReturn(null);
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				String[].class, null);
		assertThat(actual, is(nullValue()));
	}

    @Test
	public void testResolveAttributeViaMatrixParam() {
		when(requestContext.getQueryParameters()).thenReturn(params);
		when(requestContext.getMatrixParameters()).thenReturn(matrixParams);
		when(params.getValues("foo")).thenReturn(null);
		when(params.getValues("foo")).thenReturn(Arrays.asList("baa"));
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				String[].class, null);
		assertThat(((String[]) actual)[0], is("baa"));
	}
}
