package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.RequestContext;
import org.analogweb.core.DefaultResultAttributes;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.exception.NotAvairableScopeException;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultResultAttributesTest {

    private DefaultResultAttributes resultAttributes;
    private RequestContext requestContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        requestContext = mock(RequestContext.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.analogweb.core.DefaultResultAttributes#setValueOfQuery(org.analogweb.RequestContext, java.lang.String, java.lang.String, java.lang.Object)}
     * .
     */
    @Test
    public void testSetValueOfQuery() {
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        doNothing().when(placer2).putAttributeValue(requestContext, "foo", "baa");

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.setValueOfQuery(requestContext, "request", "foo", "baa");

        verify(placer2).putAttributeValue(requestContext, "foo", "baa");
    }

    @Test
    public void testSetValueOfQueryWithNullPlacer() {
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        doNothing().when(placer2).putAttributeValue(requestContext, "foo", "baa");

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.setValueOfQuery(requestContext, null, "foo", "baa");

        verify(placer2).putAttributeValue(requestContext, "foo", "baa");
    }

    @Test
    public void testSetValueOfQueryWithNullRequestContext() {
        thrown.expect(AssertionFailureException.class);
        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.setValueOfQuery(null, "request", "foo", "baa");
    }

    @Test
    public void testSetValueOfQueryWithNullAttribute() {
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        doNothing().when(placer2).putAttributeValue(requestContext, null, null);

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.setValueOfQuery(requestContext, "session", null, null);

        verify(placer1).putAttributeValue(requestContext, null, null);
    }

    @Test
    public void testSetValueOfQueryWithNotAvairableScope() {
        thrown.expect(new NoDescribeMatcher<NotAvairableScopeException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof NotAvairableScopeException) {
                    NotAvairableScopeException nv = (NotAvairableScopeException) arg0;
                    assertThat(nv.getAttemptedScopeName(), is("notavairable"));
                    return true;
                }
                return false;
            }
        });
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.setValueOfQuery(requestContext, "notavairable", null, null);

    }

    @Test
    public void testRemoveValueOfQuery() {
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        doNothing().when(placer2).removeAttribute(requestContext, "foo");

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.removeValueOfQuery(requestContext, "session", "foo");

        verify(placer1).removeAttribute(requestContext, "foo");
    }

    @Test
    public void testRemoveValueOfQueryWithDefaultPlacerName() {
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        doNothing().when(placer2).removeAttribute(requestContext, "foo");

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.removeValueOfQuery(requestContext, null, "foo");

        verify(placer2).removeAttribute(requestContext, "foo");
    }

    @Test
    public void testRemoveValueOfQueryWithNullRequestContext() {
        thrown.expect(AssertionFailureException.class);
        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.removeValueOfQuery(null, "request", "foo");
    }

    @Test
    public void testRemoveValueOfQueryWithNotAvairableScope() {
        thrown.expect(new NoDescribeMatcher<NotAvairableScopeException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof NotAvairableScopeException) {
                    NotAvairableScopeException nv = (NotAvairableScopeException) arg0;
                    assertThat(nv.getAttemptedScopeName(), is("notavairable"));
                    return true;
                }
                return false;
            }
        });
        AttributesHandler placer1 = mock(AttributesHandler.class);
        AttributesHandler placer2 = mock(AttributesHandler.class);

        Map<String, AttributesHandler> placers = Maps.newEmptyHashMap();
        placers.put("session", placer1);
        placers.put("request", placer2);

        resultAttributes = new DefaultResultAttributes(placers);
        resultAttributes.removeValueOfQuery(requestContext, "notavairable", "foo");

    }
}
