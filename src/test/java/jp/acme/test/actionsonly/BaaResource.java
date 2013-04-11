package jp.acme.test.actionsonly;

import org.analogweb.annotation.On;

@On
public class BaaResource {

    @On("anything")
    public String doAnything() {
        return "forward";
    }

    public String notAction() {
        return "no!";
    }

}
