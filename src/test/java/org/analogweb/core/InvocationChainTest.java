package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.analogweb.Invocation;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.Before;
import org.junit.Test;

public class InvocationChainTest {

    private final Log log = Logs.getLog(InvocationChainTest.class);
    private Invocation root;
    private InvocationMetadata metadata;

    @Before
    public void setUp() throws Exception {
        root = mock(Invocation.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testInvoke() {
        List<InvocationInterceptor> interceptors = new ArrayList<InvocationInterceptor>();
        final List<Integer> footprints = new ArrayList<Integer>();
        interceptors.add(new AbstractInvocationInterceptor() {

            @Override
            public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
                footprints.add(1);
                Object result = invocation.invoke();
                footprints.add(2);
                return result;
            }
        });
        interceptors.add(new AbstractInvocationInterceptor() {

            @Override
            public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
                footprints.add(3);
                Object result = invocation.invoke();
                footprints.add(4);
                return result;
            }
        });
        InvocationChain chain = InvocationChain.create(root, metadata, interceptors);
        Object result = new Object();
        when(root.invoke()).thenReturn(result);
        Object actual = chain.invoke();
        assertThat(actual, is(result));
        assertThat(footprints.size(), is(4));
        assertThat(footprints.get(0), is(1));
        assertThat(footprints.get(1), is(3));
        assertThat(footprints.get(2), is(4));
        assertThat(footprints.get(3), is(2));
    }

    @Test
    public void testInvokeWithException() {
        List<InvocationInterceptor> interceptors = new ArrayList<InvocationInterceptor>();
        final List<Integer> footprints = new ArrayList<Integer>();
        interceptors.add(new AbstractInvocationInterceptor() {

            @Override
            public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
                footprints.add(1);
                Object result = null;
                try {
                    result = invocation.invoke();
                } catch (IllegalArgumentException e) {
                    log.debug("Exception!", e);
                }
                footprints.add(2);
                return result;
            }
        });
        interceptors.add(new AbstractInvocationInterceptor() {

            @Override
            public Object onInvoke(Invocation invocation, InvocationMetadata metadata) {
                footprints.add(3);
                Object result = invocation.invoke();
                footprints.add(4);
                return result;
            }
        });
        InvocationChain chain = InvocationChain.create(root, metadata, interceptors);
        Object result = new Object();
        when(root.invoke()).thenReturn(result);
        when(root.invoke()).thenThrow(new IllegalArgumentException());
        Object actual = chain.invoke();
        assertThat(actual, is(nullValue()));
        assertThat(footprints.size(), is(3));
        assertThat(footprints.get(0), is(1));
        assertThat(footprints.get(1), is(3));
        assertThat(footprints.get(2), is(2));
    }
}
