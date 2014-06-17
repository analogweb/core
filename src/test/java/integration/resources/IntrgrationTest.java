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
	public void setUp(){
	}
	
	@Test
	public void test(){
		app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
		ResponseResult actual = app.request("/helloworld", "GET");
		assertThat(actual.getStatus(),is(200));
		actual = app.request("/helloworld", "POST");
		assertThat(actual.getStatus(),is(405));
	}
}
