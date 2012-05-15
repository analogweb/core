package org.analogweb.core;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.analogweb.Direction;
import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionFormatterAware;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.exception.DirectionEvaluationException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefaultDirectionHandlerTest {
    
    private DefaultDirectionHandler handler;
    
    private RequestContext context;
    private RequestAttributes attributes;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        handler = new DefaultDirectionHandler();
        context = mock(RequestContext.class);
        attributes = mock(RequestAttributes.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testHandleResult() throws Exception {
        Direction result = mock(Direction.class);
        
        handler.handleResult(result, null,context, attributes);
        
        verify(result).render(context);

    }

    @Test
    public void testHandleResultWithoutDirectionResult() throws Exception {
        thrown.expect(DirectionEvaluationException.class);
        
        handler.handleResult(null, null,context, attributes);
    }

    @Test
    public void testHandleResultWithDirectionFormatterAware() throws Exception {
        DirectionFormatterAware<?> result = mock(DirectionFormatterAware.class);
        DirectionFormatter formatter = mock(DirectionFormatter.class);
        
        handler.handleResult(result, formatter,context, attributes);
        
        verify(result).attach(formatter);
        verify(result).render(context);
    }

    @Test
    public void testHandleResultWithIOException() throws Exception {
        Direction result = mock(Direction.class);

        thrown.expect(DirectionEvaluationException.class);
        thrown.expect(hasDirection(result));

        doThrow(new IOException()).when(result).render(context);

        handler.handleResult(result, null,context, attributes);
    }

    private static Matcher<?> hasDirection(final Direction actionResult) {

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
