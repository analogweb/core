package org.analogweb.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.analogweb.core.ApplicationSpecifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class SuffixTest {

    private ApplicationSpecifier suffix;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {

    }

    @Test
    public void testMatch() {
        suffix = new ApplicationSpecifier("");
        assertTrue(suffix.match("/foo/baa"));
    }

    @Test
    public void testMatch_NotMatch() {
        suffix = new ApplicationSpecifier("");
        assertFalse(suffix.match("/foo/baa.do"));
    }

    @Test
    public void testMatch_ExistingMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertTrue(suffix.match("/foo/baa.do"));
    }

    @Test
    public void testMatch_ExistingNotMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertFalse(suffix.match("/foo/baa"));
    }

    @Test
    public void testInit_StartWithoutComma() {
        thrown.expect(RuntimeException.class);
        suffix = new ApplicationSpecifier("do");
    }

    @Test
    public void testMatch_RootPathIsAlwaysNotMatch() {
        suffix = ApplicationSpecifier.NONE;
        assertFalse(suffix.match("/"));
    }

}
