package org.analogweb.core;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
		assertThat(mappedPath.match(other), is(true));
	}

	@Test
	public void testDefineWithMethodNotEquals() {
		thrown.expect(RequestMethodUnsupportedException.class);
		String root = "/foo";
		String path = "/baa/something";
		String[] requestMethods = { "POST", "GET" };
		RequestPathDefinition mappedPath = RequestPathDefinition.define(root, path, requestMethods);
		RequestPath other = mock(RequestPath.class);
		when(other.getActualPath()).thenReturn("/foo/baa/something");
		when(other.getRequestMethod()).thenReturn("DELETE");
		mappedPath.match(other);
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
	public void testDefineWithNullEditPath() {
		thrown.expect(InvalidRequestPathException.class);
		String root = "/foo";
		String path = null;
		RequestPathDefinition.define(root, path);
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
	public void testMatchStatically() {
		assertThat("/foo/baa/something", is(matchFor("/foo/baa/something")));
		assertThat("/foo/baa/something.html", is(matchFor("/foo/baa/something.html")));
		assertThat("/foo/baa/something", is(not(matchFor("/foo/something"))));
		assertThat("/foo/baa/something", is(not(matchFor("/foo/baa/something.html"))));
		assertThat("/foo/baa/something", is(not(matchFor("/foo/baa/a/something"))));
		assertThat("/foo/baa/something", is(not(matchFor("/foo/baa/something/a/"))));
	}

	@Test
	public void testMatchWithWindCard() {
		assertThat("/foo/baa/*", is(matchFor("/foo/baa/something")));
		assertThat("/foo/baa/*", is(matchFor("/foo/baa/something/anything")));
		assertThat("/foo/*/baa", is(matchFor("/foo/something/baa")));
		assertThat("/foo/*/baa", is(matchFor("/foo/something/anything/baa")));
		assertThat("/foo/*/baa", is(matchFor("/foo/something/anything/nothing/baa")));
		assertThat("*/foo/baa/*", is(matchFor("/something/foo/baa/anything")));
		assertThat("*/foo/*/baa", is(matchFor("/baz/foo/something/baa")));
		assertThat("*/foo/*/baa", is(matchFor("/baa/baz/foo/something/baa")));
		assertThat("*.html", is(matchFor("/baa/baz.html")));
		assertThat("*.html", is(matchFor("/baa/baz/foo.html")));
		assertThat("*.html", is(matchFor("/baa/baz/foo.html/baa.html")));
		assertThat("/foo/*.html", is(matchFor("/foo/baa.html")));
		assertThat("/foo/*.html", is(not(matchFor("/foo/baa"))));
		assertThat("*.html", is(not(matchFor("/baa/baz"))));
		assertThat("*.html", is(not(matchFor("/baa/html"))));
		assertThat("*.html", is(not(matchFor("/baa/baz.htm"))));
		assertThat("*/foo/*/baa", is(not(matchFor("/baa/baz/foo/something/baa/baz"))));
		assertThat("/foo/baa/*", is(not(matchFor("/something/foo/baa/anything"))));
		assertThat("/foo/baa/*", is(not(matchFor("/foo/something/baa/anything"))));
		assertThat("/foo/baa/*", is(not(matchFor("/something/baa/foo/anything"))));
		assertThat("/foo/*/baa", is(not(matchFor("/foo/something/baa/baz"))));
	}

	@Test
	public void testMatchWithPlaceHolder() {
		assertThat("/foo/baa/{baz}", is(matchFor("/foo/baa/something")));
		assertThat("/foo/baa/{baz}/{hoge}", is(matchFor("/foo/baa/something/anything")));
		assertThat("/foo/baa/{baz}/{hoge}/fuga", is(matchFor("/foo/baa/something/anything/fuga")));
		assertThat("/foo/baa/{baz}", is(not(matchFor("/foo/baa/something/anything"))));
		assertThat("/foo/baa/{baz}", is(not(matchFor("/foo/baa"))));
		assertThat("/foo/baa/{baz}/{hoge}", is(not(matchFor("/foo/baa/something/anything/nothing"))));
		assertThat("/foo/baa/{baz}/{hoge}/fuga", is(not(matchFor("/foo/baa/something/anything/else"))));
		assertThat("/foo/baa/{baz}/{hoge}/fuga", is(not(matchFor("/foo/baa/something/anything/nothing/fuga"))));
		assertThat("/foo/baa/{baz", is(not(matchFor("/foo/baa/something"))));
	}

	@Test
	public void testMatchWithRegexPlaceHolder() {
		assertThat("/foo/baa/baz$<[0-9]>", is(matchFor("/foo/baa/1")));
		assertThat("/foo/baa/{baz}/hoge$<[a-c]>", is(matchFor("/foo/baa/something/a")));
		assertThat("/foo/baa/baz$<[0-9]>", is(not(matchFor("/foo/baa/something/anything"))));
		assertThat("/foo/baa/{baz}", is(not(matchFor("/foo/baa"))));
		assertThat("/foo/baa/{baz}/hoge$<[a-c]>", is(not(matchFor("/foo/baa/something/anything/d"))));
	}

	@Test
	public void testNotMatchWithRequestMethods() {
		thrown.expect(RequestMethodUnsupportedException.class);
		String root = "/foo";
		String path = "/baa/something";
		RequestPathMetadata actual = RequestPathDefinition.define(root, path, new String[] { "POST", "GET" });
		RequestPath actualSame = mock(RequestPath.class);
		when(actualSame.getActualPath()).thenReturn("/foo/baa/something");
		when(actualSame.getRequestMethod()).thenReturn("PUT");
		actual.match(actualSame);
	}

	Matcher<String> matchFor(final String requested) {
		return new TypeSafeMatcher<String>() {

			@Override
			public void describeTo(Description description) {
			}

			@Override
			protected boolean matchesSafely(String item) {
				RequestPathMetadata definedPath = RequestPathDefinition.define("", item, new String[] { "GET" });
				DefaultRequestPath requestedPath = new DefaultRequestPath(URI.create("/"), URI.create(requested),
						"GET");
				log.debug(String.format("Compare %s to %s", definedPath.toString(), requestedPath.toString()));
				return definedPath.match(requestedPath);
			}
		};
	}
}
