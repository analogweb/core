package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.analogweb.core.AssertionFailureException;
import org.analogweb.util.Assertion;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AssertionTest {

	@Test
	public void testAssertNotNull() {
		try {
			Assertion.notNull(null, "foo");
			fail("expected exception not occured.");
		} catch (AssertionFailureException e) {
			assertThat(e.getRequiredName(), is("foo"));
		}
	}

	@Test
	public void testAssertNotNullWithArg() {
		Assertion.notNull("baa", "foo");
		// nothing to do.
	}
}
