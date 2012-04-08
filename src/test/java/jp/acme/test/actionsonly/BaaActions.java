package jp.acme.test.actionsonly;


import org.analogweb.annotation.On;
import org.analogweb.core.direction.Forward;


@On
public class BaaActions {

    @On
    public Forward doSomething() {
        return Forward.to("/foo");
    }

    @On("anything")
    public String doAnything() {
        return "forward";
    }

    public String notAction() {
        return "no!";
    }

}
