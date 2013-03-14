package org.analogweb.core;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.analogweb.Response;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseFormatterAware;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.ResponseEvaluationException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefaultResponseHandlerTest {

    private DefaultResponseHandler handler;

    private RequestContext context;
    private ResponseContext response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        handler = new DefaultResponseHandler();
        context = mock(RequestContext.class);
        response = mock(ResponseContext.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testHandleResult() throws Exception {
        Response result = mock(Response.class);

        handler.handleResult(result, null, context, response);

        verify(result).render(context, response);

    }

    @Test
    public void testHandleResultWithoutResponseResult() throws Exception {
        thrown.expect(ResponseEvaluationException.class);

        handler.handleResult(null, null, context, response);
    }

    @Test
    public void testHandleResultWithResponseFormatterAware() throws Exception {
        ResponseFormatterAware<?> result = mock(ResponseFormatterAware.class);
        ResponseFormatter formatter = mock(ResponseFormatter.class);

        handler.handleResult(result, formatter, context, response);

        verify(result).attach(formatter);
        verify(result).render(context, response);
    }

    @Test
    public void testHandleResultWithIOException() throws Exception {
        Response result = mock(Response.class);

        thrown.expect(ResponseEvaluationException.class);
        thrown.expect(hasResponse(result));

        doThrow(new IOException()).when(result).render(context, response);

        handler.handleResult(result, null, context, response);
    }

    private static Matcher<?> hasResponse(final Response actionResult) {

        return new BaseMatcher<Response>() {

            @Override
            public boolean matches(Object item) {
                if (item instanceof ResponseEvaluationException) {
                    ResponseEvaluationException are = (ResponseEvaluationException) item;
                    return are.getActionResult() == actionResult;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

}
