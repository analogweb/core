package org.analogweb.core.response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.Response;
import org.analogweb.core.DefaultResponse;
import org.analogweb.WebApplicationException;
import org.analogweb.core.DefaultResponseEntity;
import org.junit.Before;
import org.junit.Test;

/**
 * @author y2k2mt
 */
public class AcceptableTest {

	private RequestContext context;
	private ResponseContext response;
	private Headers headers;

	@Before
	public void setUp() throws Exception {
		context = mock(RequestContext.class);
		response = mock(ResponseContext.class);
		headers = mock(Headers.class);
	}

	@Test
	public void testRenderAcceptableXMLWithReplacedFormatter() throws Exception {
		final Member m = new Member("snowgoose", 34);
		Acceptable a = Acceptable.as(m);
		a.map(new Renderable() {

			@Override
			public Response render(RequestContext context,
					ResponseContext response) throws IOException,
					WebApplicationException {
				return new DefaultResponse(new DefaultResponseEntity(
						"write with XML"));
			}
		}, "text/xml");
		final String actual = schenarioRender(" text/xml", m, a);
		assertThat(actual, is("write with XML"));
	}

	@Test
	public void testRenderAcceptableSecondXML() throws Exception {
		final Member m = new Member("snowgoose", 34);
		final String actual = schenarioRender(
				" text/x-dvi; q=0.8, application/xml, */*", m);
		assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
	}

	@Test
	public void testRenderAcceptableXMLWithQuality() throws Exception {
		final Member m = new Member("snowgoose", 34);
		final String actual = schenarioRender(
				" text/x-dvi; q=0.8, text/xml; q=6, */*", m);
		assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
	}

	@Test
	public void testRenderAcceptableJSON() throws Exception {
		final Member m = new Member("snowgoose", 34);
		final String actual = schenarioRender(
				" application/json, application/xml", m);
		assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
	}

	@Test
	public void testRenderAcceptableAny() throws Exception {
		final Member m = new Member("snowgoose", 34);
		// final String accept = " text/x-dvi,image/png, */*";
		when(context.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList("text/x-dvi", "image/png", "*/*"));
		// when(request.getHeader("Accept")).thenReturn(accept);
		// Response writer = new DefaultResponseWriter();
		// when(response.getResponse()).thenReturn(writer);
		final Renderable anyResponse = mock(Renderable.class);
		Acceptable.as(m).mapToAny(anyResponse).render(context, response);
		verify(anyResponse).render(context, response);
	}

	@Test
	public void testRenderSwitAcceptableAny() throws Exception {
		final Member m = new Member("snowgoose", 34);
		final String actual = schenarioRender(" text/x-dvi,image/png, */*", m);
		// mapped json.
		assertThat(actual, is("{\"age\": 34,\"name\": \"snowgoose\"}"));
	}

	@Test
	public void testRenderSwitAcceptableAnyWithReplacedFormatter()
			throws Exception {
		final Member m = new Member("snowgoose", 34);
		Acceptable a = Acceptable.as(m);
		a.mapToAny(new Renderable() {

			@Override
			public Response render(RequestContext context,
					ResponseContext response) throws IOException,
					WebApplicationException {
				return new DefaultResponse(new DefaultResponseEntity(
						"write with ANY"));
			}
		});
		final String actual = schenarioRender(" text/x-dvi,image/png, */*", m,
				a);
		// mapped any.
		assertThat(actual, is("write with ANY"));
	}

	@Test
	public void testRenderSwitchedAcceptable() throws Exception {
		final Member m = new Member("snowgoose", 34);
		when(context.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList("text/x-dvi", "image/png", "application/json"));
		final Renderable replaceResponse = mock(Renderable.class);
		Acceptable.as(m).map(replaceResponse, "application/json")
				.render(context, response);
		verify(replaceResponse).render(context, response);
	}

	@Test
	public void testSelectAcceptableOne() throws Exception {
		final Member m = new Member("snowgoose", 34);
		when(context.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList("text/html", "text/x-dvi", "image/png",
						"application/json"));
		final Renderable jsonResponse = mock(Renderable.class);
		final Renderable htmlResponse = mock(Renderable.class);
		Acceptable acceptable = Acceptable.as(m)
				.map(jsonResponse, "application/json")
				.map(htmlResponse, "text/html");
		Renderable actual = acceptable.selectAcceptableOne(context);
		assertThat(actual, is(htmlResponse));
	}

	@Test
	public void testSelectAcceptableNoOne() throws Exception {
		final Member m = new Member("snowgoose", 34);
		when(context.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList("text/x-dvi", "image/png"));
		final Renderable jsonResponse = mock(Renderable.class);
		final Renderable htmlResponse = mock(Renderable.class);
		Acceptable acceptable = Acceptable.as(m)
				.map(jsonResponse, "application/json")
				.map(htmlResponse, "text/html");
		Renderable actual = acceptable.selectAcceptableOne(context);
		assertThat(actual, is((Renderable) HttpStatus.NOT_ACCEPTABLE));
	}

	@Test
	public void testRenderNotAcceptable() throws Exception {
		final Member m = new Member("snowgoose", 34);
		final String actual = schenarioRender(" text/x-dvi,image/png, text/*",
				m);
		assertThat(actual, is(""));
		verify(response).setStatus(406);
	}

	@Test
	public void testRenderNotAcceptable2() throws Exception {
		final Member m = new Member("snowgoose", 34);
		when(context.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList("text/x-dvi", "image/png", "*/*"));
		final Renderable replaceResponse = mock(Renderable.class);
		Acceptable.as(m).mapToAny(replaceResponse).render(context, response);
		verify(replaceResponse).render(context, response);
	}

	private String schenarioRender(final String accept, final Member m)
			throws Exception {
		return schenarioRender(accept, m, Acceptable.as(m));
	}

	private String schenarioRender(final String accept, final Member m,
			final Acceptable a) throws Exception {
		when(context.getRequestHeaders()).thenReturn(headers);
		Headers responseHeaders = mock(Headers.class);
		when(response.getResponseHeaders()).thenReturn(responseHeaders);
		when(headers.getValues("Accept")).thenReturn(
				Arrays.asList(accept.split(",")));
		Response writer = a.render(context, response);
		Object entity = writer.getEntity().entity();
		if (entity instanceof byte[]) {
			return new String((byte[]) entity);
		} else {
			return (String) entity;
		}
	}

	@Test
	public void testComparator() {
		final List<String> accepts = Arrays.asList(" text/plain",
				" application/*;level=1", " application/json", " */*",
				" text/html;q=1", " text/*");
		Collections.sort(accepts, new Acceptable.AcceptHeaderComparator());
		assertThat(accepts.size(), is(6));
		assertThat(accepts.get(0), is(" text/html;q=1"));
		assertThat(accepts.get(1), is(" text/plain"));
		assertThat(accepts.get(2), is(" application/json"));
		assertThat(accepts.get(3), is(" application/*;level=1"));
		assertThat(accepts.get(4), is(" text/*"));
		assertThat(accepts.get(5), is(" */*"));
	}

	@XmlRootElement
	public static class Member {

		@XmlElement
		private String name;
		@XmlElement
		private int age;

		public Member() {
			super();
		}

		public Member(final String name, final int age) {
			super();
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}
	}
}
