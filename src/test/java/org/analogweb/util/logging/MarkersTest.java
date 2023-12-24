package org.analogweb.util.logging;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.analogweb.core.AssertionFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MarkersTest extends Markers {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValueOf() {
        SimpleMarker foo = SimpleMarker.valueOf("foo");
        SimpleMarker foo2 = SimpleMarker.valueOf("foo");
        SimpleMarker baa = SimpleMarker.valueOf("baa");
        assertThat(foo, is(foo2));
        assertThat(foo, is(not(baa)));
        assertFalse(foo.equals("foo"));
    }

    @Test
    public void testWithoutName() {
        thrown.expect(AssertionFailureException.class);
        SimpleMarker.valueOf(null);
    }
}
