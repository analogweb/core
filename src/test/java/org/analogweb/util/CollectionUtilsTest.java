package org.analogweb.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class CollectionUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(CollectionUtils.isEmpty(Collections.emptyList()));
        assertTrue(CollectionUtils.isEmpty(null));
        assertFalse(CollectionUtils.isEmpty(Arrays.asList("")));
        assertFalse(CollectionUtils.isEmpty(Arrays.asList("a", "b")));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(CollectionUtils.isNotEmpty(Collections.emptyList()));
        assertFalse(CollectionUtils.isNotEmpty(null));
        assertTrue(CollectionUtils.isNotEmpty(Arrays.asList("")));
        assertTrue(CollectionUtils.isNotEmpty(Arrays.asList("a", "b")));
    }

    @Test
    public void testIndexOf() {
        assertThat(CollectionUtils.indexOf(Arrays.asList("a", "b", "c"), 1), is("b"));
        assertThat(CollectionUtils.indexOf(Arrays.asList("a", "b", "c"), 3), is(nullValue()));
        assertThat(CollectionUtils.indexOf(Arrays.asList("a", "b", "c"), -1), is(nullValue()));
        assertThat(CollectionUtils.indexOf(Arrays.asList("a", "b", "c"), 3, "d"), is("d"));
    }
}
