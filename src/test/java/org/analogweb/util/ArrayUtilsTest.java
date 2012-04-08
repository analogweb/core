package org.analogweb.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.analogweb.util.ArrayUtils;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ArrayUtilsTest {

    @Test
    public void testIsNotEmpty() {
        String[] anArray = new String[] { "a", "b", "c" };
        assertTrue(ArrayUtils.isNotEmpty(anArray));
    }

    @Test
    public void testIsNotEmptyWithEmptyArray() {
        Object[] anArray = new Object[0];
        assertFalse(ArrayUtils.isNotEmpty(anArray));
    }

    @Test
    public void testIsNotEmptyWithNullArray() {
        Object[] anArray = null;
        assertFalse(ArrayUtils.isNotEmpty(anArray));
    }

    @Test
    public void testIsEmpty() {
        String[] anArray = new String[] { "a", "b", "c" };
        assertFalse(ArrayUtils.isEmpty(anArray));
    }

    @Test
    public void testIsEmptyWithEmptyArray() {
        Object[] anArray = new Object[0];
        assertTrue(ArrayUtils.isEmpty(anArray));
    }

    @Test
    public void testIsEmptyWithNullArray() {
        Object[] anArray = null;
        assertTrue(ArrayUtils.isEmpty(anArray));
    }

    @Test
    public void testNewArray() {
        String[] expected = { "a", "b", "c" };
        String[] actual = ArrayUtils.newArray("a", "b", "c");
        assertArrayEquals(actual, expected);
    }

    @Test
    public void testClone() {
        String[] expected = { "a", "b", "c" };
        String[] actual = ArrayUtils.clone(String.class, expected);
        assertArrayEquals(actual, expected);
        assertNotSame(expected, actual);
    }

    @Test
    public void testCloneArray() {
        String[] expected = { "a", "b", "c" };
        String[] actual = ArrayUtils.clone(String.class, expected);
        assertArrayEquals(actual, expected);
        assertNotSame(expected, actual);
    }

    @Test
    public void testCloneEmpty() {
        String[] expected = {};
        String[] actual = ArrayUtils.clone(String.class);
        assertArrayEquals(actual, expected);
        assertNotSame(expected, actual);
    }

    @Test
    public void testCloneNull() {
        String[] actual = ArrayUtils.clone(String.class, (String[]) null);
        assertNull(actual);
    }

    @Test
    public void testCloneArrayWithNullType() {
        String[] expected = { "a", "b", "c" };
        String[] actual = ArrayUtils.clone(null, expected);
        assertArrayEquals(actual, expected);
        assertSame(expected, actual);
    }

    @Test
    public void testAddToArray() {
        String[] expected = { "a", "b", "c", "d" };
        String[] actual = ArrayUtils.add(String.class, "d", "a", "b", "c");

        assertArrayEquals(actual, expected);
    }

    @Test
    public void testAddMultipleValueToArray() {
        String[] expected = { "a", "b", "c", "d", "e", "f" };
        String[] actual = ArrayUtils.add(String.class, "d", "a", "b", "c");
        actual = ArrayUtils.add(String.class, "e", actual);
        actual = ArrayUtils.add(String.class, "f", actual);

        assertArrayEquals(actual, expected);
    }

    @Test
    public void testAddNullToArray() {
        String[] expected = { "a", "b", "c", null };
        String[] actual = ArrayUtils.add(String.class, null, "a", "b", "c");

        assertArrayEquals(actual, expected);
    }
}
