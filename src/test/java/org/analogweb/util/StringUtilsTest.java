package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author snowgoose
 */
public class StringUtilsTest {

	@Test
	public void testIsEmpty() {
		assertFalse(StringUtils.isEmpty("test!"));
		assertFalse(StringUtils.isEmpty("t !"));
		assertFalse(StringUtils.isEmpty(" test!"));
		assertTrue(StringUtils.isEmpty(" "));
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(StringUtils.isEmpty("     "));
	}

	@Test
	public void testIsNotEmpty() {
		assertTrue(StringUtils.isNotEmpty("test!"));
		assertTrue(StringUtils.isNotEmpty("t !"));
		assertTrue(StringUtils.isNotEmpty(" test!"));
		assertFalse(StringUtils.isNotEmpty(" "));
		assertFalse(StringUtils.isNotEmpty(null));
		assertFalse(StringUtils.isNotEmpty("     "));
	}

	@Test
	public void testSplit() {
		assertTrue(StringUtils.split(null, '/').isEmpty());
		assertThat(StringUtils.split("a\tb\nc", null),
				is(Arrays.asList("a", "b", "c")));
		assertThat(StringUtils.split("a\tb\n\tc", null),
				is(Arrays.asList("a", "b", "c")));
		assertThat(StringUtils.split("a.b.c", '.'),
				is(Arrays.asList("a", "b", "c")));
		assertThat(StringUtils.split("a..b.c", '.'),
				is(Arrays.asList("a", "b", "c")));
		assertThat(StringUtils.split("a:b:c", '.'), is(Arrays.asList("a:b:c")));
		assertThat(StringUtils.split("a b c", ' '),
				is(Arrays.asList("a", "b", "c")));
	}

	@Test
	public void testPartition() {
		String target = "request:hoge";
		List<String> actual = StringUtils.partition(1, ':', target);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("request"));
		assertThat(actual.get(1), is("hoge"));
		target = "request";
		actual = StringUtils.partition(1, ':', target);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("request"));
		assertNull(actual.get(1));
		target = "request:hoge:attr";
		actual = StringUtils.partition(1, ':', target);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("request"));
		assertThat(actual.get(1), is("hoge:attr"));
		target = "request:hoge:attr:bar";
		actual = StringUtils.partition(1, ':', target);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("request"));
		assertThat(actual.get(1), is("hoge:attr:bar"));
		target = "request:hoge:attr";
		actual = StringUtils.partition(2, ':', target);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is("request:hoge"));
		assertThat(actual.get(1), is("attr"));
	}

	@Test
	public void testSubstring() {
		String target = "abc";
		String actual = StringUtils.substring(target, 1);
		assertThat(actual, is("bc"));
		actual = StringUtils.substring(target, 2);
		assertThat(actual, is("c"));
		actual = StringUtils.substring(target, 3);
		assertThat(actual, is(""));
		actual = StringUtils.substring(target, 4);
		assertNull(actual);
		actual = StringUtils.substring(target, 0, 1);
		assertThat(actual, is("a"));
		actual = StringUtils.substring(target, 0, 2);
		assertThat(actual, is("ab"));
		actual = StringUtils.substring(target, 0, 3);
		assertThat(actual, is("abc"));
		actual = StringUtils.substring(target, 0, 4);
		assertThat(actual, is("abc"));
		actual = StringUtils.substring(target, 1, 1);
		assertThat(actual, is(""));
		actual = StringUtils.substring(target, 1, 2);
		assertThat(actual, is("b"));
		actual = StringUtils.substring(target, 1, 3);
		assertThat(actual, is("bc"));
		actual = StringUtils.substring(target, 1, 4);
		assertThat(actual, is("bc"));
		actual = StringUtils.substring(target, 2, 1);
		assertNull(actual);
		actual = StringUtils.substring(target, 2, 2);
		assertThat(actual, is(""));
		actual = StringUtils.substring(target, 2, 3);
		assertThat(actual, is("c"));
		actual = StringUtils.substring(target, 2, 4);
		assertThat(actual, is("c"));
	}

	@Test
	public void testSubstringWithEmptyArgs() {
		String actual = StringUtils.substring(StringUtils.EMPTY, 0);
		assertThat(actual, is(StringUtils.EMPTY));
		actual = StringUtils.substring(null, 0);
		assertNull(actual);
		actual = StringUtils.substring(null, 0, 1);
		assertNull(actual);
		actual = StringUtils.substring(null, 1, 0);
		assertNull(actual);
	}

	@Test
	public void testTrimToEmpty() {
		assertThat(StringUtils.trimToEmpty("foo "), is("foo"));
		assertThat(StringUtils.trimToEmpty(" "), is(StringUtils.EMPTY));
		assertThat(StringUtils.trimToEmpty("           "),
				is(StringUtils.EMPTY));
		assertThat(StringUtils.trimToEmpty(" foo"), is("foo"));
		assertThat(StringUtils.trimToEmpty(" foo "), is("foo"));
		assertThat(StringUtils.trimToEmpty("          foo            "),
				is("foo"));
		assertThat(StringUtils.trimToEmpty(null), is(StringUtils.EMPTY));
		assertThat(StringUtils.trimToEmpty(""), is(StringUtils.EMPTY));
	}

	@Test
	public void testCharAt() {
		String value = "foo";
		assertThat(StringUtils.charAt(0, value), is('f'));
		assertThat(StringUtils.charAt(1, value), is('o'));
		assertThat(StringUtils.charAt(3, value), is(Character.MIN_VALUE));
		assertThat(StringUtils.charAt(-1, value), is(Character.MIN_VALUE));
		assertThat(StringUtils.charAt(0, null), is(Character.MIN_VALUE));
	}

	@Test
	public void testJoin() {
		assertThat(StringUtils.join(',', "a", "b", "c"), is("a,b,c"));
		assertThat(StringUtils.join(',', "a"), is("a"));
		assertThat(StringUtils.join(',', "a", "b", "c,"), is("a,b,c,"));
		assertThat(StringUtils.join(','), is(""));
	}
}
