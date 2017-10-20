package jp.acme.test.actionsonly;

import org.analogweb.annotation.Route;

@Route
public class BaaResource {

	@Route("anything")
	public String doAnything() {
		return "forward";
	}

	public String notAction() {
		return "no!";
	}
}
