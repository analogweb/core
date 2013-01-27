package org.analogweb.util;

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

}
