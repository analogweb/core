package org.analogweb.core.direction;

import static org.mockito.Mockito.*;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.analogweb.RequestContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HtmlTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        Html html = Html.as("pathOfHtmlTemplate", new HashMap<String, Object>());
        RequestContext context = mock(RequestContext.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(context.getResponse()).thenReturn(response);
        html.render(context);
        
    }

}
