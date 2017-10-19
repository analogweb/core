package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.analogweb.ApplicationProcessor;
import org.analogweb.Precedence;
import org.junit.Test;

public class PrecedenceComparatorTest {

	@Test
	public void testCompare() {
		Precedence p1 = mock(Precedence.class);
		Precedence p2 = mock(Precedence.class);
		Precedence p3 = mock(ApplicationProcessor.class);
		Precedence p4 = mock(Precedence.class);
		Precedence p5 = mock(Precedence.class);
		when(p1.getPrecedence()).thenReturn(1);
		when(p2.getPrecedence()).thenReturn(2);
		when(p3.getPrecedence()).thenReturn(3);
		when(p4.getPrecedence()).thenReturn(Precedence.LOWEST);
		when(p5.getPrecedence()).thenReturn(Precedence.HIGHEST);
		List<? extends Precedence> list = Arrays.asList(p4, p1, p2, p5, p3);
		Collections.sort(list, new PrecedenceComparator<Precedence>());
		assertThat(list.get(0), is(p5));
		assertThat(list.get(1), is(p1));
		assertThat(list.get(2), is(p2));
		assertThat(list.get(3), is(p3));
		assertThat(list.get(4), is(p4));
	}

	@Test
	public void testCompareSamePrecidence() {
		Precedence p1 = mock(Precedence.class);
		Precedence p2 = mock(Precedence.class);
		Precedence p3 = mock(Precedence.class);
		Precedence p4 = mock(Precedence.class);
		Precedence p5 = mock(Precedence.class);
		when(p1.getPrecedence()).thenReturn(1);
		when(p2.getPrecedence()).thenReturn(2);
		when(p3.getPrecedence()).thenReturn(1);// Same as p1
		when(p4.getPrecedence()).thenReturn(Precedence.LOWEST);
		when(p5.getPrecedence()).thenReturn(Precedence.HIGHEST);
		List<? extends Precedence> list = Arrays.asList(p4, p3, p2, p5, p1);
		Collections.sort(list, new PrecedenceComparator<Precedence>());
		assertThat(list.get(0), is(p5));
		// List order relative.
		assertThat(list.get(1), is(p3));
		assertThat(list.get(2), is(p1));
		assertThat(list.get(3), is(p2));
		assertThat(list.get(4), is(p4));
	}

	@Test
	public void testDifferentType() {
		ApplicationProcessor p1 = mock(ApplicationProcessor.class);
		ApplicationProcessor p2 = mock(ApplicationProcessor.class);
		ApplicationProcessor p3 = mock(ApplicationProcessor.class);
		when(p1.getPrecedence()).thenReturn(1);
		when(p2.getPrecedence()).thenReturn(2);
		when(p3.getPrecedence()).thenReturn(3);
		List<ApplicationProcessor> list = Arrays.asList(p2, p3, p1);
		Collections.sort(list, new PrecedenceComparator<Precedence>());
		assertThat(list.get(0), is(p1));
		assertThat(list.get(1), is(p2));
		assertThat(list.get(2), is(p3));
	}
}
