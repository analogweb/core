package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.Test;

public class MatrixParametersTest {

	private MatrixParameters params;

	@Test
	public void test() {
		URI requestURI = URI
				.create("http://some.where/thing;paramA=1;paramB=6542");
		params = new MatrixParameters(requestURI);
		List<String> actual = params.getValues("paramA");
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is("1"));
		actual = params.getValues("paramB");
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is("6542"));
	}

	@Test
	public void testContainsPath() {
		URI requestURI = URI
				.create("http://example.com/res/categories;name=foo/objects;name=green/?page=1");
		params = new MatrixParameters(requestURI);
		List<String> actual = params.getValues("name");
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("foo/objects"));
		assertThat(actual.get(1), is("green/?page=1"));
	}

	@Test
	public void testNoMatrixParameters() {
		URI requestURI = URI
				.create("http://some.where/thing?paramA=1&paramB=6542");
		params = new MatrixParameters(requestURI);
		List<String> actual = params.getValues("paramA");
		assertThat(actual.size(), is(0));
		actual = params.getValues("paramB");
		assertThat(actual.size(), is(0));
	}
}
