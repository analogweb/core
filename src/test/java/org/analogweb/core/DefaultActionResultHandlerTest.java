package org.analogweb.core;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;


import org.analogweb.Direction;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.core.DefaultDirectionHandler;
import org.analogweb.exception.DirectionEvaluationException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultActionResultHandlerTest {

    private DefaultDirectionHandler handler;
    private Direction result;
    private RequestContext context;
    private RequestAttributes attributes;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        handler = new DefaultDirectionHandler();
        result = mock(Direction.class);
        context = mock(RequestContext.class);
        attributes = mock(RequestAttributes.class);
    }

    @Test
    public void testHandleResult() throws Exception {
        doNothing().when(result).render(context);

        handler.handleResult(result, context, attributes);
    }

    @Test
    public void testHandleResultWithIOException() throws Exception {
        thrown.expect(DirectionEvaluationException.class);
        thrown.expect(hasActionResult(result));

        doThrow(new IOException()).when(result).render(context);

        handler.handleResult(result, context, attributes);
    }

    private static Matcher<?> hasActionResult(final Direction actionResult) {

        return new BaseMatcher<Direction>() {

            @Override
            public boolean matches(Object item) {
                if (item instanceof DirectionEvaluationException) {
                    DirectionEvaluationException are = (DirectionEvaluationException) item;
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
