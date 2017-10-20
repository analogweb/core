package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.analogweb.util.PropertyResourceBundleMessageResource;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class PropertyResourceBundleMessageResourceTest {

	private PropertyResourceBundleMessageResource resource;

	@Test
	public void testGetMessage() {
		String baseName = getClass().getCanonicalName() + "-1";
		resource = new PropertyResourceBundleMessageResource(baseName);
		String actual = resource.getMessage("foo");
		assertThat(actual, is("baa"));
	}

	@Test
	public void testGetMessageNotAvairable() {
		String baseName = getClass().getCanonicalName() + "-1";
		resource = new PropertyResourceBundleMessageResource(baseName);
		String actual = resource.getMessage("boo");
		assertThat(actual, is("boo"));
	}

	@Test
	public void testGetMessageWithLocale() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.US);
			String baseName = getClass().getCanonicalName() + "-2";
			resource = new PropertyResourceBundleMessageResource(baseName);
			String actual = resource.getMessage("foo", Locale.JAPAN);
			assertThat(actual, is("ばー"));
			// missing locale.
			actual = resource.getMessage("foo", Locale.ENGLISH);
			assertThat(actual, is("baa"));
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	public void testGetMessageWithFormat() {
		String baseName = getClass().getCanonicalName() + "-3";
		resource = new PropertyResourceBundleMessageResource(baseName);
		String actual = resource.getMessage("foo", "baa", "baz");
		assertThat(actual, is("baa is baz"));
	}

	@Test
	public void testGetMessageWithFormatAndLocale() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.US);
			String baseName = getClass().getCanonicalName() + "-4";
			resource = new PropertyResourceBundleMessageResource(baseName);
			String actual = resource.getMessage("foo", Locale.JAPAN, "baa",
					"baz");
			assertThat(actual, is("baa は baz"));
			// missing locale.
			actual = resource.getMessage("foo", Locale.ENGLISH, "baa", "baz");
			assertThat(actual, is("baa is baz"));
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	public void testGetMessageResourceContainsMultiByteCharactors() {
		String baseName = getClass().getCanonicalName() + "-5";
		resource = new PropertyResourceBundleMessageResource(baseName);
		String actual = resource.getMessage("foo");
		assertThat(actual, is("ばー"));
	}

	@Test
	public void testGetBulcMessages() {
		String baseName = getClass().getCanonicalName() + "-1";
		resource = new PropertyResourceBundleMessageResource(baseName);
		String actual = null;
		int i = 0;
		long start = System.currentTimeMillis();
		for (i = 0; i < 1000000; i++) {
			actual = resource.getMessage("foo");
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("get [%s] messages on [%s] ms.", i,
				end - start));
		assertThat(actual, is("baa"));
	}
}
