package org.analogweb.core.response;

import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void test() throws Exception {
		thrown.expect(UnsupportedOperationException.class);
		Html html = Html
				.as("pathOfHtmlTemplate", new HashMap<String, Object>());
		RequestContext context = mock(RequestContext.class);
		ResponseContext response = mock(ResponseContext.class);
		html.render(context, response);
	}
}
