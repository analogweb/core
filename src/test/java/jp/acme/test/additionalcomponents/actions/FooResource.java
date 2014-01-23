package jp.acme.test.additionalcomponents.actions;

import org.analogweb.annotation.Route;

@Route
public class FooResource {

    @Route
    public String doSomething() {
        return "forward";
    }

    @Route("anything")
    public String doAnything() {
        return "forward";
    }
}
