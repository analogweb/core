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
        assertFalse(CollectionUtils.isEmpty(Arrays.asList("a","b")));
    }

}
