package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.analogweb.Cookies;
import org.analogweb.util.StringUtils;
import org.junit.Test;

public class RequestCookiesTest {

    private RequestCookies cookies;

    @Test
    public void testGetCookie() {
        cookies = new RequestCookies("foo=baa;SomeID=IDSIDS;isA");
        Cookies.Cookie actual = cookies.getCookie("foo");
        assertThat(actual.getValue(), is("baa"));
        actual = cookies.getCookie("SomeID");
        assertThat(actual.getValue(), is("IDSIDS"));
        actual = cookies.getCookie("someID");
        assertThat(actual, is(nullValue()));
        actual = cookies.getCookie("isA");
        assertThat(actual.getValue(), is(StringUtils.EMPTY));
    }

    @Test
    public void testGetCookieWithEmptyHeader() {
        cookies = new RequestCookies(null);
        Cookies.Cookie actual = cookies.getCookie("foo");
        assertThat(actual, is(nullValue()));
        actual = cookies.getCookie("SomeID");
        assertThat(actual, is(nullValue()));
        actual = cookies.getCookie("someID");
        assertThat(actual, is(nullValue()));
    }

}
