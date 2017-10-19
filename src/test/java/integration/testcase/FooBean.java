package integration.testcase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FooBean {

	@XmlElement
	private String baa = "baz";

	public String getBaa() {
		return this.baa;
	}
}
