package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;


import org.analogweb.RequestPathMetadata;
import org.analogweb.core.RequestPathDefinition;
import org.analogweb.exception.InvalidRequestPathException;
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
        RequestPathMetadata mappedPath = RequestPathDefinition.define(root, path);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
    }

    @Test
    public void testDefineWithMethod() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
        assertThat(mappedPath.getRequestMethods().get(0), is("POST"));
        assertThat(mappedPath.getRequestMethods().get(1), is("GET"));
    }

    @Test
    public void testDefineWithMethodEquals() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);

        RequestPathMetadata other = mock(RequestPathMetadata.class);
        when(other.getActualPath()).thenReturn("/foo/baa/something");
        when(other.getRequestMethods()).thenReturn(Arrays.asList("POST"));

        assertTrue(mappedPath.match(other));
    }

    @Test
    public void testDefineWithMethodNotEquals() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = { "POST", "GET" };
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);

        RequestPathMetadata other = mock(RequestPathMetadata.class);
        when(other.getActualPath()).thenReturn("/foo/baa/something");
        when(other.getRequestMethods()).thenReturn(Arrays.asList("DELETE"));

        assertFalse(mappedPath.match(other));
    }

    @Test
    public void testDefineWithNullMethod() {
        String root = "/foo";
        String path = "/baa/something";
        String[] requestMethods = null;
        RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);
        assertThat(mappedPath.getActualPath(), is("/foo/baa/something"));
        assertTrue(mappedPath.getRequestMethods().isEmpty());
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
    public void testMatch() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithRequestMethods() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path,
                new String[] { "request" });

        root = "/foo";
        path = "baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path, new String[] {
                "session", "request" });

        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchNoStrict() {
        String root = "/foo/";
        String path = "baa/something.do";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "foo";
        path = "/baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithDifferentRootStrict() {
        String root = "/foo/baa";
        String path = "something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithWindCard() {
        String root = "/foo";
        String path = "/baa/*";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithContainsWindCard() {
        String root = "/foo";
        String path = "/baa/*/baz";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything/baz";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testNotMatchWithContainsWindCard() {
        String root = "/foo";
        String path = "/baa/*/baz";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything/baz/bad";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testNotMatchWithWindCard() {
        String root = "/foo";
        String path = "/baa/*";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baasomething";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testMatchWithPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz}";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/{something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));

        root = "/foo";
        path = "/baa/{something";
        actual = RequestPathDefinition.define(root, path);

        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithMultiplePlaceHolder() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/anything";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithMultipleFirstPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{hoge}/{baz}";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testMatchWithNotMatchMultiplePlaceHolder() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/baa";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testMatchWithMultiplePlaceHolderOnly() {
        String root = "/foo";
        String path = "/{hoge}/{baz}";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertTrue(actual.match(actualSame));
    }

    @Test
    public void testNotMatchWithMultiplePlaceHolderOnly() {
        String root = "/foo";
        String path = "/{hoge}/{baz}";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa";
        RequestPathMetadata actualNotSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualNotSame);
        assertFalse(actual.match(actualNotSame));

        root = "/foo";
        path = "/baa/baz/bad";
        actualNotSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualNotSame);
        assertFalse(actual.match(actualNotSame));
    }

    @Test
    public void testMatchWithNotMatchMultiplePlaceHolderNumber() {
        String root = "/foo";
        String path = "/{hoge}/{baz}/baa";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/something/anything/else";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testNotMatch() {
        String root = "/foo";
        String path = "baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "baa/anything";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testNotMatchWithRequestMethods() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path, new String[] {
                "request", "application" });

        root = "/foo";
        path = "baa/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path, new String[] {
                "session", "cookie" });

        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testNotRootMatch() {
        String root = "/foo/baa/";
        String path = "baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "baz/something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        assertFalse(actual.match(actualSame));
    }

    @Test
    public void testNotEquals() {
        String root = "/foo";
        String path = "/baa/something";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        String otherPath = "/foo/baa/something";

        assertFalse(actual.equals(otherPath));
    }

    @Test
    public void testNotMatchWithWorngPlaceHolder() {
        String root = "/foo";
        String path = "/baa/{baz";
        RequestPathMetadata actual = RequestPathDefinition.define(root, path);

        root = "/foo";
        path = "/baa/{something";
        RequestPathMetadata actualSame = RequestPathDefinition.define(root, path);

        log.debug("actual path : " + actual);
        log.debug("other actual path : " + actualSame);
        assertFalse(actual.match(actualSame));

        root = "/foo";
        path = "/baa/baz}";
        actual = RequestPathDefinition.define(root, path);

        assertFalse(actual.match(actualSame));
    }

}
