package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;


import org.analogweb.core.ApplicationSpecifier;
import org.analogweb.core.DefaultRequestPath;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRequestPathTest {

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    public void testGetPath() {
        when(request.getRequestURI()).thenReturn("/foo/baa/baz.do");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
    }

    @Test
    public void testGetPathWithRequestMethod() {
        when(request.getRequestURI()).thenReturn("/foo/baa/baz.do");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
        assertThat(actual.getRequestMethods().size(), is(1));
        assertThat(actual.getRequestMethods().get(0), is("GET"));
    }

    @Test
    public void testGetPathWithoutSuffix() {
        when(request.getRequestURI()).thenReturn("/foo/baa/baz");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.NONE));
    }

    @Test
    public void testGetPathContainsJsessionId() {

        when(request.getRequestURI()).thenReturn(
                "/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertThat(actual.getActualPath(), is("/baa"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
    }

    @Test
    public void testPathThrowgh() {
        when(request.getRequestURI()).thenReturn(
                "/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertTrue(actual.pathThrowgh(""));
    }

    @Test
    public void testPathThrowghWithActionPath() {
        when(request.getRequestURI()).thenReturn(
                "/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertFalse(actual.pathThrowgh(".do"));
    }

    @Test
    public void testNotPathThrowghWithActionPath() {
        when(request.getRequestURI()).thenReturn(
                "/foo/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");
        when(request.getContextPath()).thenReturn("/foo");

        DefaultRequestPath actual = new DefaultRequestPath(request);
        assertFalse(actual.pathThrowgh(""));
    }

    @Test
    public void testIdentifiedByActualPath() {
        when(request.getRequestURI()).thenReturn("/foo/baa");
        when(request.getContextPath()).thenReturn("/foo");
        DefaultRequestPath pathA = new DefaultRequestPath(request);
        DefaultRequestPath pathB = new DefaultRequestPath(request);
        assertTrue(pathA.match(pathB));
    }

}
