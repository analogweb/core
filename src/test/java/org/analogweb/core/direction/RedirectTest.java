package org.analogweb.core.direction;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.exception.MissingRequirmentsException;
import org.analogweb.junit.NoDescribeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class RedirectTest {

    private RequestContext context;
    private Headers responseHeader;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        context = mock(RequestContext.class);
        responseHeader = mock(Headers.class);
        when(context.getResponseHeaders()).thenReturn(responseHeader);
    }

    @Test
    public void testRender() throws Exception {

        Redirect.to("/some/foo.rn").render(context);

        verify(context).setResponseStatus(302);
        verify(responseHeader).putValue("Location", "/some/foo.rn");
    }

    @Test
    public void testRenderWithNotReturnCode() throws Exception {

        // not redirect response code.
        Redirect.to("/some/foo.rn").resoposeCode(404).render(context);

        verify(context).setResponseStatus(302);
        verify(responseHeader).putValue("Location", "/some/foo.rn");
    }

    @Test
    public void testRenderWithNullContext() throws Exception {

        thrown.expect(AssertionFailureException.class);

        Redirect.to("/some/foo.rn").render(null);

    }

    @Test
    public void testRenderWithParameter() throws Exception {

        Redirect.to("/some/foo.rn").addParameter("foo", "baa").addParameter("hoge", "fuga")
                .render(context);

        verify(context).setResponseStatus(302);
        verify(responseHeader).putValue("Location", "/some/foo.rn?foo=baa&hoge=fuga");
    }

    @Test
    public void testRenderWithParametarizedURLAndParameter() throws Exception {
        // sort parameter name by natural order.
        Redirect.to("/some/foo.rn?boo=baz").addParameter("hoge", "fuga").addParameter("foo", "baa")
                .encodeWith("ISO-8859-1").render(context);

        verify(context).setResponseStatus(302);
        verify(responseHeader).putValue("Location", "/some/foo.rn?boo=baz&foo=baa&hoge=fuga");

    }

    @Test
    public void testRenderWithEmptyPath() throws Exception {
        thrown.expect(new NoDescribeMatcher<MissingRequirmentsException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof MissingRequirmentsException) {
                    MissingRequirmentsException ex = (MissingRequirmentsException) arg0;
                    assertThat(ex.getRequirment(), is("redirect path"));
                    assertThat(ex.getUnresolvableInvocationResult(), is(nullValue()));
                    return true;
                }
                return false;
            }
        });

        Redirect.to(null);
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(Redirect.to("/foo/bar"), is(Redirect.to("/foo/bar")));
        assertThat(Redirect.to("/foo/bar"), is(not(Redirect.to("/foo/bar2"))));
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(Redirect.to("/foo/bar").hashCode(), is(Redirect.to("/foo/bar").hashCode()));
        assertThat(Redirect.to("/foo/bar").hashCode(), is(not(Redirect.to("/foo/bar2").hashCode())));
    }

}
