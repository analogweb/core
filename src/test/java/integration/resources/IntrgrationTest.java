package integration.resources;

import static org.analogweb.core.fake.FakeApplication.fakeApplication;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.analogweb.ReadableBuffer;
import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.DefaultReadableBuffer;
import org.analogweb.core.fake.FakeApplication;
import org.analogweb.core.fake.ResponseResult;
import org.analogweb.util.Maps;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class IntrgrationTest {

    private FakeApplication app;

    @After
    public void tearDown() {
        app.shutdown();
    }

    @Test
    public void testFound() {
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
        ResponseResult actual = app.request("/helloworld", "GET");
        assertThat(actual.getStatus(), is(200));
        assertThat(actual.toBody(), is("Hello World"));
        assertThat(actual.getResponseHeader().get("Content-Type").get(0),
                is("text/plain; charset=UTF-8"));
    }

    @Test
    public void testMethodNotAllowed() {
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
        ResponseResult actual = app.request("/helloworld", "POST");
        assertThat(actual.getStatus(), is(405));
    }

    @Test
    public void testPathvariable() {
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
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
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
        ResponseResult actual = app.request("/helloUserAgent", "GET",
                Maps.newHashMap("User-Agent", Arrays.asList("JUnit")));
        assertThat(actual.getStatus(), is(200));
        assertThat(actual.toBody(), is("Hello World JUnit"));
        assertThat(actual.getResponseHeader().get("Content-Type").get(0),
                is("text/plain; charset=UTF-8"));
    }

    @Test
    public void testPostFormToBean() {
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
        ReadableBuffer body = DefaultReadableBuffer.readBuffer(new ByteArrayInputStream("baa=foo".getBytes()));
        ResponseResult actual = app
                .request(
                        "/helloBean",
                        "POST",
                        Maps.newHashMap("Content-Type",
                                Arrays.asList("application/x-www-form-urlencoded")), body);
        assertThat(actual.getStatus(), is(200));
        assertThat(
                actual.toBody(),
                is("foo"));
        assertThat(actual.getResponseHeader().get("Content-Type").get(0),
                is("text/plain; charset=UTF-8"));
    }

    @Test
    public void testVoid() {
        app = fakeApplication(DefaultApplicationProperties.properties("integration.testcase"));
        ResponseResult actual = app.request("/helloNothing", "GET");
        assertThat(actual.getStatus(), is(204));
        actual = app.request("/helloNull", "GET");
        assertThat(actual.getStatus(), is(204));
    }

}
