package org.analogweb.core;

import static org.hamcrest.core.Is.is;
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
		URI uri = new URI("http://somehost:8080/foo/baa/baz.html");
		URI baseUri = new URI("http://somehost:8080/foo");
		DefaultRequestPath actual = new DefaultRequestPath(baseUri, uri, "GET");
		assertThat(actual.getActualPath(), is("/baa/baz.html"));
	}

	@Test
	public void testGetPathOnRootContext() throws Exception {
		URI uri = new URI("http://somehost:8080/baa/baz");
		URI baseUri = new URI("http://somehost:8080/");
		DefaultRequestPath actual = new DefaultRequestPath(baseUri, uri, "GET");
		assertThat(actual.getActualPath(), is("/baa/baz"));
	}

	@Test
	public void testGetPathWithRequestMethod() throws Exception {
		URI uri = new URI("http://somehost:8080/foo/baa/baz?abc=def");
		URI baseUri = new URI("http://somehost:8080/foo/");
		DefaultRequestPath actual = new DefaultRequestPath(baseUri, uri, "GET");
		assertThat(actual.getActualPath(), is("/baa/baz"));
		assertThat(actual.getRequestMethod(), is("GET"));
	}

	@Test
	public void testGetPathWithoutSuffix() throws Exception {
		URI uri = new URI("http://somehost:8080/foo/baa/baz?hoge=fuga");
		URI baseUri = new URI("http://somehost:8080/foo");
		DefaultRequestPath actual = new DefaultRequestPath(baseUri, uri, "POST");
		assertThat(actual.getActualPath(), is("/baa/baz"));
	}

	@Test
	public void testGetPathContainsJsessionId() throws Exception {
		URI uri = new URI(
				"http://somehost:8080/foo/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");
		URI baseUri = new URI("http://somehost:8080/foo");
		DefaultRequestPath actual = new DefaultRequestPath(baseUri, uri, "POST");
		assertThat(actual.getActualPath(), is("/baa"));
	}

	@Test
	public void testNotPathThrowghWithContextRootPath() throws Exception {
		URI uri = new URI(
				"http://somehost:8080/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");
		DefaultRequestPath actual = new DefaultRequestPath(null, uri, "Post");
		assertThat(actual.getActualPath(), is("/baa"));
	}

	@Test
	public void testIdentifiedByActualPath() throws Exception {
		URI uri = new URI(
				"http://somehost:8080/foo/baa;jsessionid=1A26E401D812045AF2D9150891DA01B3");
		URI baseUri = new URI("http://somehost:8080/foo");
		DefaultRequestPath pathA = new DefaultRequestPath(baseUri, uri, "POST");
		DefaultRequestPath pathB = new DefaultRequestPath(baseUri, uri, "POST");
		assertTrue(pathA.match(pathB));
	}
}
