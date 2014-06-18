package integration.resources;

import static org.analogweb.core.fake.FakeApplication.fakeApplication;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.fake.FakeApplication;
import org.analogweb.core.fake.ResponseResult;
import org.junit.Before;
import org.junit.Test;

public class IntrgrationTest {

	private FakeApplication app;

	@Before
	public void setUp() {
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
}
