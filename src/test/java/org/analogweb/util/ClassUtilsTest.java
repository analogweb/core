package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.analogweb.util.ClassUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class ClassUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testForNameQuietly() {
        Class<?> actual = ClassUtils.forNameQuietly(ClassUtilsTest.class.getCanonicalName());
        assertThat(actual.getCanonicalName(), is(this.getClass().getCanonicalName()));
    }

    @Test
    public void testForNameQuietlyNotAviableClass() {
        Class<?> actual = ClassUtils.forNameQuietly("jp.snowgoose.not.exists.Class");
        assertNull(actual);
    }

    @Test
    public void testForNameQuietlyNullClass() {
        thrown.expect(NullPointerException.class);
        ClassUtils.forNameQuietly(null);
    }
}
