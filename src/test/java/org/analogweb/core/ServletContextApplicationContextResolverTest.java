package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ServletContextApplicationContextResolverTest {

    private ServletContextApplicationContextResolver resolver;
    private ServletContext context;

    @Before
    public void setUp() throws Exception {
        context = mock(ServletContext.class);
        resolver = new ServletContextApplicationContextResolver(context);
    }

    /**
     * Test method for {@link org.analogweb.core.ServletContextApplicationContextResolver#resolve(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testResolve() {
        when(context.getAttribute("foo")).thenReturn(BigDecimal.ONE);
        Number actual = resolver.resolve(Number.class, "foo");
        assertThat((BigDecimal) actual, is(BigDecimal.ONE));
    }

    /**
     * Test method for {@link org.analogweb.core.ServletContextApplicationContextResolver#resolve(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testResolveUnResolvable() {
        when(context.getAttribute("foo")).thenReturn(null);
        Number actual = resolver.resolve(Number.class, "foo");
        assertThat((BigDecimal) actual, is(nullValue()));
    }

    /**
     * Test method for {@link org.analogweb.core.ServletContextApplicationContextResolver#resolve(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testResolveUnResolvableType() {
        when(context.getAttribute("foo")).thenReturn(BigDecimal.ONE);
        Integer actual = resolver.resolve(Integer.class, "foo");
        assertThat(actual, is(nullValue()));
    }

}
