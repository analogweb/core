package jp.acme.test.additionalcomponents.actions;

import org.analogweb.annotation.On;

@On
public class FooResource {

    @On
    public String doSomething() {
        return "forward";
    }

    @On("anything")
    public String doAnything() {
        return "forward";
    }

}
