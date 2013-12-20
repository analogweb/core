package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.core.InvalidRequestPathException;
import org.analogweb.core.RequestMethodUnsupportedException;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class RequestPathDefinitionTest {

    private static final Log log = Logs.getLog(RequestPathDefinitionTest.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDefine() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
        assertThat(mappedPath.getRequestMethods().get(0), is("GET"));
        assertThat(mappedPath.getRequestMethods().get(1), is("POST"));
    }

    @Test
    public void testDefineWithMethod() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET", "PUT" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
        assertThat(mappedPath.getRequestMethods().get(0), is("POST"));
        assertThat(mappedPath.getRequestMethods().get(1), is("GET"));
        assertThat(mappedPath.getRequestMethods().get(2), is("PUT"));
    }

    @Test
    public void testDefineWithMethodEquals() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);

        RequestPath other = mock(RequestPath.class);
        when(other.getActualPath()).thenReturn("/foo/baa/something");
        when(other.getRequestMethod()).thenReturn("POST");

        assertTrue(mappedPath.match(other));
    }

    @Test
    public void testDefineWithMethodNotEquals() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);

        RequestPath other = mock(RequestPath.class);
        when(other.getActualPath()).thenReturn("/foo/baa/something");
        when(other.getRequestMethod()).thenReturn("DELETE");

        assertFalse(mappedPath.match(other));
    }

    @Test
    public void testDefineWithNullMethod() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = null;
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
        assertThat(mappedPath.getRequestMethods().size(), is(2));
        assertThat(mappedPath.getRequestMethods().get(0), is("GET"));
        assertThat(mappedPath.getRequestMethods().get(1), is("POST"));
    }

    @Test
    public void testDefineContainsAsterisc() {
        thrown.expect(new NoDescribeMatcher<InvalidRequestPathException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof InvalidRequestPathException) {
                    InvalidRequestPathException iv = (InvalidRequestPathException) arg0;
                    assertThat(iv.getInvalidRootPath(), is("/foo/*"));
                    assertThat(iv.getInvalidPath(), is("/baa/something"));
                    return true;
                }
                return false;
            }
        });
        String root = "/foo/*";
        String path = "/baa/something";
        RequestPathDefinition.define(root, path);
    }

    @Test
    public void testDefineWithNoRootSlash() {
        thrown.expect(InvalidRequestPathException.class);
        String root = "foo/*";
        String path = "/baa/something";
        RequestPathDefinition.define(root, path);
    }

    @Test
    public void testDefineWithNoSubPathSlash() {
        String root = "foo";
        String path = "baa/something";
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
    }

    @Test
    public void testDefineWithWildCard() {
        String root = "foo";
        String path = "baa/*/something";
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/*/something"));
    }

    @Test
    public void testDefineEndsWithWildCard() {
        String root = "foo";
        String path = "baa/anything/*";
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/anything/*"));
    }

    @Test
    public void testDefineWithSuffix() {
        String root = "";
        String path = "/baa/something.do";
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        // ignore suffix.
        assertThat(mappedPath.getActualPath(), is("/baa/something"));
    }

    @Test
    public void testDefineWithNullRootPath() {
        String root = null;
        String path = "baa/something.do";
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/baa/something"));
    }

    @Test
    public void testDefineWithNullEditPath() {
        thrown.expect(InvalidRequestPathException.class);
        String root = "/foo";
        String path = null;
        RequestPathDefinition.define(root, path);
    }

    @Test
    public void testDefineWithEmptyRootPath() {
        String root = "";
        String path = "baa/something.do";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);
        assertThat(actual.getActualPath(), is("/baa/something"));
    }

    @Test
    public void testDefineWithEmptyEditPath() {
        thrown.expect(InvalidRequestPathException.class);
        String root = "/foo";
        String path = "";
        RequestPathDefinition.define(root, path);
    }

    @Test
    public void testDefineWithPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz}";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);
        assertThat(actual.getActualPath(), is("/foo/baa/{baz}"));
    }

    @Test
    public void testDefineWithManyPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz}/{hoge}/";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);
        assertThat(actual.getActualPath(), is("/foo/baa/{baz}/{hoge}/"));
    }

    @Test
    public void testMatchNoStrict() {
        String root = "/foo/";
        String path = "baa/something";
        String expected = "/foo/baa/something.do";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testMatchWithWindCard() {
        String root = "/foo";
        String path = "/baa/*";
        String expected = "/foo/baa/something";
        assertMatch(root, path, expected);
    }

    @Test
    public void testMatchWithContainsWindCard() {
        String root = "/foo";
        String path = "/baa/*/baz";
        String expected = "/foo/baa/something/anything/baz";
        assertMatch(root, path, expected);
    }

    @Test
    public void testNotMatchWithContainsWindCard() {
        String root = "/foo";
        String path = "/baa/*/baz";
        String expected = "/foo/baa/something/anything/baz/bad";
        assertMatch(root, path, expected);
    }

    @Test
    public void testNotMatchWithWindCard() {
        String root = "/foo";
        String path = "/baa/*";
        String expected = "/foo/baasomething";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testMatchWithPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz}";
        String expected = "/foo/baa/{something";
        assertMatch(root, path, expected);

        root = "/foo";
        path = "/baa/{something";
        assertMatch(root, path, expected);
    }

    @Test
    public void testMatchWithMultiplePlaceHolder() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/anything";
        String expected = "/foo/baa/something/anything";
        assertMatch(root, path, expected);
    }

    @Test
    public void testMatchWithMultipleFirstPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{hoge}/{baz}";
        String expected = "/foo/baa/something/anything";
        assertMatch(root, path, expected);
    }

    @Test
    public void testMatchWithNotMatchMultiplePlaceHolder() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/baa";
        String expected = "/foo/baa/something/anything";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testMatchWithMultiplePlaceHolderOnly() {
        String root = "/foo";
        String path = "/{hoge}/{baz}";
        String expected = "/foo/baa/something";
        assertMatch(root, path, expected);
    }

    @Test
    public void testNotMatchWithMultiplePlaceHolderOnly() {
        String root = "/foo";
        String path = "/{hoge}/{baz}";
        String expected = "/foo/baa";
        assertNotMatch(root, path, expected);

        expected = "/foo/baa/baz/bad";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testMatchWithNotMatchMultiplePlaceHolderNumber() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/baa";
        String expected = "/foo/something/anything/else";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testNotMatchWithRequestMethods() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path, new String[] {
                "POST", "GET" });

        RequestPath actualSame = mock(RequestPath.class);
        when(actualSame.getActualPath()).thenReturn("/foo/baa/something");
        when(actualSame.getRequestMethod()).thenReturn("PUT");

        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testNotRootMatch() {
        String root = "/foo/baa/";
        String path = "baa/something";
        String expected = "/foo/baz/something";
        assertNotMatch(root, path, expected);
    }

    @Test
    public void testNotMatchWithWorngPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz";
        String expected = "/foo/baa/{something";
        assertNotMatch(root, path, expected);

        expected = "/foo/baa/baz}";
        assertNotMatch(root, path, expected);
    }

    void assertMatch(String root, String path, String expected) {
        assertPath(root, path, expected, true);
    }

    void assertNotMatch(String root, String path, String expected) {
        assertPath(root, path, expected, false);
    }

    void assertPath(String root, String path, String expected, boolean expectMatch) {
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        RequestPath actualSame = mock(RequestPath.class);
        when(actualSame.getActualPath()).thenReturn(expected);
        when(actualSame.getRequestMethod()).thenReturn("GET");

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        if (expectMatch) {
            assertTrue(actual.match(actualSame));
        } else {
            assertFalse(actual.match(actualSame));
        }
    }

    @Test
    public void testFulFillSatisfied() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path,
                new String[] { "GET" });

        RequestPath requestPath = mock(RequestPath.class);
        when(requestPath.getRequestMethod()).thenReturn("GET");

        // do nothing.
        mappedPath.fulfill(requestPath);
    }

    @Test
    public void testFulFillUnSatisfied() {
        thrown.expect(new NoDescribeMatcher<RequestMethodUnsupportedException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof RequestMethodUnsupportedException) {
                    RequestMethodUnsupportedException ex = (RequestMethodUnsupportedException) arg0;
                    assertThat(ex.getMetadata().getActualPath(), is("/foo/baa/something"));
                    assertThat(ex.getRequestedMethod(), is("GET"));
                    assertThat(ex.getDefinedMethods().containsAll(Arrays.asList("POST")), is(true));
                    return true;
                }
                return false;
            }
        });
        String root = "/foo";
        String path = "/baa/something";
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path,
                new String[] { "POST" });

        RequestPath requestPath = mock(RequestPath.class);
        when(requestPath.getRequestMethod()).thenReturn("GET");

        mappedPath.fulfill(requestPath);
    }

}
