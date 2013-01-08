package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultRequestPathTest {

    @Test
    public void testGetPath() throws Exception {

        URI uri = new URI("http://somehost:8080/foo/baa/baz.do");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "GET");
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
    }

    @Test
    public void testGetPathWithRequestMethod() throws Exception {

        URI uri = new URI("http://somehost:8080/foo/baa/baz.do?abc=def");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "GET");
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
        assertThat(actual.getMethod(), is("GET"));
    }

    @Test
    public void testGetPathWithoutSuffix() throws Exception {

        URI uri = new URI("http://somehost:8080/foo/baa/baz?hoge=fuga");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "POST");
        assertThat(actual.getActualPath(), is("/baa/baz"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.NONE));
    }

    @Test
    public void testGetPathContainsJsessionId() throws Exception {

        URI uri = new URI(
                "http://somehost:8080/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "POST");
        assertThat(actual.getActualPath(), is("/baa"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf(".do")));
    }

    @Test
    public void testPathThrowgh() throws Exception {

        URI uri = new URI(
                "http://somehost:8080/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "POST");
        assertTrue(actual.pathThrowgh(""));
    }

    @Test
    public void testPathThrowghAndRequestMethodLowerCase() throws Exception {
        URI uri = new URI(
                "http://somehost:8080/foo/baa.do;jsessionid=1A26E401D812045AF2D9150891DA01B3");

        DefaultRequestPath actual = new DefaultRequestPath("/foo", uri, "Post");
        assertFalse(actual.pathThrowgh(".do"));
    }

    @Test
    public void testNotPathThrowghWithContextRootPath() throws Exception {
        URI uri = new URI("http://somehost:8080/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");

        DefaultRequestPath actual = new DefaultRequestPath("", uri, "Post");
        assertThat(actual.getActualPath(), is("/baa"));
        assertThat(actual.getSuffix(), is(ApplicationSpecifier.valueOf("")));
    }

    @Test
    public void testIdentifiedByActualPath() throws Exception {
        URI uri = new URI(
                "http://somehost:8080/foo/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");

        DefaultRequestPath pathA = new DefaultRequestPath("/foo", uri, "POST");
        DefaultRequestPath pathB = new DefaultRequestPath("/foo", uri, "POST");

        assertTrue(pathA.match(pathB));
    }

}
