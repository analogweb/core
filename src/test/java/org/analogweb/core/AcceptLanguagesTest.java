package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.junit.Before;
import org.junit.Test;

public class AcceptLanguagesTest {

    private RequestContext request;
    private Headers headers;

    @Before
    public void setUp() {
        request = mock(RequestContext.class);
        headers = mock(Headers.class);
    }

    @Test
	public void testGetLocale() {
		when(request.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept-Language"))
				.thenReturn(
						Arrays.asList("en-ca,en;q=0.8,en-us;q=0.6,de-de;q=0.4,de;q=0.2,ja_JP"));
		AcceptLanguages lang = new AcceptLanguages(request);
		List<Locale> expected = Arrays.asList(Locale.CANADA, Locale.JAPAN,
				Locale.ENGLISH, Locale.US, Locale.GERMANY, Locale.GERMAN);
		assertThat(lang.getLocales(), is(expected));
		assertThat(lang.getLocale(), is(Locale.CANADA));
	}

    @Test
	public void testGetLocaleWithEmptyHeader() {
		when(request.getRequestHeaders()).thenReturn(headers);
		when(headers.getValues("Accept-Language")).thenReturn(null);
		AcceptLanguages lang = new AcceptLanguages(request);
		assertThat(lang.getLocales().isEmpty(), is(true));
		assertThat(lang.getLocale(), is(nullValue()));
	}
}
