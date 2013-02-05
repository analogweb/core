package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.analogweb.InvocationProcessor;
import org.analogweb.Precedence;
import org.junit.Test;

public class PrecedenceComparatorTest {

    @Test
    public void test() {
        Precedence p1 = mock(Precedence.class);
        Precedence p2 = mock(Precedence.class);
        Precedence p3 = mock(InvocationProcessor.class);
        Precedence p4 = mock(Precedence.class);
        Precedence p5 = mock(Precedence.class);

        when(p1.getPrecedence()).thenReturn(1);
        when(p2.getPrecedence()).thenReturn(2);
        when(p3.getPrecedence()).thenReturn(3);
        when(p4.getPrecedence()).thenReturn(Precedence.LOWEST);
        when(p5.getPrecedence()).thenReturn(Precedence.HIGHEST);

        List<? extends Precedence> list = Arrays.asList(p4, p1, p2, p5, p3);
        Collections.sort(list, new PrecedenceComparator());

        assertThat(list.get(0), is(p5));
        assertThat(list.get(1), is((Precedence) p3));
        assertThat(list.get(2), is(p2));
        assertThat(list.get(3), is(p1));
        assertThat(list.get(4), is(p4));

    }

    @Test
    public void testDifferentType() {
        InvocationProcessor p1 = mock(InvocationProcessor.class);
        InvocationProcessor p2 = mock(InvocationProcessor.class);
        InvocationProcessor p3 = mock(InvocationProcessor.class);

        when(p1.getPrecedence()).thenReturn(1);
        when(p2.getPrecedence()).thenReturn(2);
        when(p3.getPrecedence()).thenReturn(3);

        List<InvocationProcessor> list = Arrays.asList(p2, p3, p1);
        Collections.sort(list, new PrecedenceComparator());

        assertThat(list.get(0), is(p3));
        assertThat(list.get(1), is(p2));
        assertThat(list.get(2), is(p1));

    }

}
