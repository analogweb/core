package integration.testcase;

import static org.analogweb.core.response.BasicResponses.text;

import org.analogweb.Renderable;
import org.analogweb.annotation.Bean;
import org.analogweb.annotation.Get;
import org.analogweb.annotation.PathVariable;
import org.analogweb.annotation.Post;
import org.analogweb.annotation.Put;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.annotation.Route;
import org.analogweb.core.MediaTypes;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.core.response.Text;

/**
 * Hello Analog Web World!
 *
 * @author snowgoose
 */
@Route("/")
public class HelloWorld {

    @Route
    @Get
    public String helloworld() {
        return "Hello World";
    }

    @Route("hello/{name}/world")
    @Get
    public Text helloworld(@PathVariable("name") String name) {
        return text(String.format("Hello %s World", name));
    }

    @Route
    @Get
    public Text helloUserAgent(@UserAgent String userAgent) {
        return text(String.format("Hello World %s", userAgent));
    }

    @Route
    @RequestFormats(MediaTypes.APPLICATION_FORM_URLENCODED)
    @Put
    @Post
    public Text helloBean(@Bean FooBean foo) {
        return text(foo.getBaa());
    }

    @Route
    @Get
    public void helloNothing() {
        // Return no content(204)
    }

    @Route
    @Get
    public Renderable helloNull() {
        // Return no content(204)
        return null;
    }

    @Route
    @Get
    public HttpStatus ok() {
        FooBean foo = new FooBean();
        return HttpStatus.OK.with(text(foo.getBaa()));
    }
}
