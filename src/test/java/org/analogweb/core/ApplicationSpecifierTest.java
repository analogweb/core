package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.analogweb.core.ApplicationSpecifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class ApplicationSpecifierTest {

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
    public void testNotMatch() {
        suffix = new ApplicationSpecifier("");
        assertFalse(suffix.match("/foo/baa.do"));
    }

    @Test
    public void testNotRootPathMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertFalse(suffix.match("/"));
    }

    @Test
    public void testExistingMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertTrue(suffix.match("/foo/baa.do"));
    }

    @Test
    public void testExistingNoRootPathMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertTrue(suffix.match("foo/baa.do"));
    }

    @Test
    public void testExistingNotMatch() {
        suffix = new ApplicationSpecifier(".do");
        assertFalse(suffix.match("/foo/baa"));
    }

    @Test
    public void testExistingNotMatchWithNull() {
        suffix = new ApplicationSpecifier(".do");
        assertFalse(suffix.match(null));
    }

    @Test
    public void testInitStartWithoutComma() {
        thrown.expect(RuntimeException.class);
        suffix = new ApplicationSpecifier("do");
    }

    @Test
    public void testMatchRootPathIsAlwaysNotMatch() {
        suffix = ApplicationSpecifier.NONE;
        assertFalse(suffix.match("/"));
    }

    @Test
    public void testEquals() {
        suffix = new ApplicationSpecifier(".do");
        Object other = new ApplicationSpecifier(".do");
        assertTrue(suffix.equals(other));
        assertThat(suffix.getSuffix().hashCode(), is(suffix.hashCode()));
        assertThat(suffix.toString(), is(".do"));
        other = new ApplicationSpecifier(".rn");
        assertFalse(suffix.equals(other));
        other = new ApplicationSpecifier("");
        assertFalse(suffix.equals(other));
        other = "";
        assertFalse(suffix.equals(other));
    }
}
