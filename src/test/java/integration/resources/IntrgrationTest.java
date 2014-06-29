package integration.resources;

import static org.analogweb.core.fake.FakeApplication.fakeApplication;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.fake.FakeApplication;
import org.analogweb.core.fake.ResponseResult;
import org.analogweb.util.Maps;
import org.junit.After;
import org.junit.Test;

public class IntrgrationTest {

	private FakeApplication app;

	@After
	public void tearDown() {
		app.shutdown();
	}

	@Test
	public void testFound() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloworld", "GET");
		assertThat(actual.getStatus(), is(200));
		assertThat(actual.toBody(), is("Hello World"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("text/plain; charset=UTF-8"));
	}

	@Test
	public void testMethodNotAllowed() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloworld", "POST");
		assertThat(actual.getStatus(), is(405));
	}

	@Test
	public void testPathvariable() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/hello/snowgooseyk/world", "GET");
		assertThat(actual.getStatus(), is(200));
		assertThat(actual.toBody(), is("Hello snowgooseyk World"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("text/plain; charset=UTF-8"));
		actual = app.request("/hello/snowgoose/yk/world", "GET");
		assertThat(actual.getStatus(), is(404));
	}

	@Test
	public void testMetaAnnotation() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloUserAgent", "GET",
				Maps.newHashMap("User-Agent", Arrays.asList("JUnit")));
		assertThat(actual.getStatus(), is(200));
		assertThat(actual.toBody(), is("Hello World JUnit"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("text/plain; charset=UTF-8"));
	}

	@Test
	public void testXmlBody() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloXml", "GET");
		assertThat(actual.getStatus(), is(200));
		assertThat(
				actual.toBody(),
				is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><fooBean><baa>baz</baa></fooBean>"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("application/xml; charset=UTF-8"));
	}

	@Test
	public void testPutXmlBody() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		InputStream body = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><fooBean><baa>baz</baa></fooBean>"
						.getBytes());
		ResponseResult actual = app.request("/helloXmlValue", "PUT",
				Maps.newHashMap("Content-Type", Arrays.asList("text/xml")),
				body);
		assertThat(actual.getStatus(), is(200));
		assertThat(actual.toBody(), is("Hello World baz"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("text/plain; charset=UTF-8"));
	}

	@Test
	public void testPutXmlBodyInvalidContent() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		InputStream body = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><fooBean><baa>baz</baa></fooBean>"
						.getBytes());
		ResponseResult actual = app.request("/helloXmlValue", "PUT",
				Maps.newHashMap("Content-Type", Arrays.asList("plain/text")),
				body);
		assertThat(actual.getStatus(), is(415));
	}

	@Test
	public void testPostFormToBean() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		InputStream body = new ByteArrayInputStream("baa=foo".getBytes());
		ResponseResult actual = app.request(
				"/helloBean",
				"POST",
				Maps.newHashMap("Content-Type",
						Arrays.asList("application/x-www-form-urlencoded")),
				body);
		assertThat(actual.getStatus(), is(200));
		assertThat(
				actual.toBody(),
				is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><fooBean><baa>foo</baa></fooBean>"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("application/xml; charset=UTF-8"));
	}

	@Test
	public void testVoid() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloNothing", "GET");
		assertThat(actual.getStatus(), is(204));
		actual = app.request("/helloNull", "GET");
		assertThat(actual.getStatus(), is(204));
	}

	@Test
	public void testStatusWithXmlBody() {
		app = fakeApplication(DefaultApplicationProperties
				.properties("integration.testcase"));
		ResponseResult actual = app.request("/ok", "GET");
		assertThat(actual.getStatus(), is(200));
		assertThat(
				actual.toBody(),
				is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><fooBean><baa>baz</baa></fooBean>"));
		assertThat(actual.getResponseHeader().get("Content-Type").get(0),
				is("application/xml; charset=UTF-8"));
	}
}
