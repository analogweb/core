package org.analogweb.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.analogweb.core.SingletonInstanceContainerAdaptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptorTest {

    private SingletonInstanceContainerAdaptor adaptor;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        adaptor = new SingletonInstanceContainerAdaptor();
    }

    @Test
    public void testGetInstance() {
        thrown.expect(UnsupportedOperationException.class);
        adaptor.getInstanceOfId("id");
    }

    @Test
    public void testGetInstanceOfType() {
        Component actual = adaptor.getInstanceOfType(Component.class);
        assertNotNull(actual);
    }

    @Test
    public void testGetInstanceOfTypeDoNotInstanticate() {
        thrown.expect(UnsupportedOperationException.class);
        adaptor.getInstanceOfType(HasntDefaultConstractorCompoent.class);
    }

    @Test
    public void testGetInstanceOfTypeDoNotAccessable() {
        thrown.expect(UnsupportedOperationException.class);
        adaptor.getInstanceOfType(DoNotAccessableCompoent.class);
    }

    @Test
    public void testGetInstanceOfTypeAlwaysReturnsSameInstance() {
        Component actual = adaptor.getInstanceOfType(Component.class);
        assertNotNull(actual);
        Component twice = adaptor.getInstanceOfType(Component.class);
        assertSame(actual, twice);
    }

    @Test
    public void testGetInstancesOfTypeAlwaysReturnsSameInstance() {
        List<Component> actual = adaptor.getInstancesOfType(Component.class);
        assertNotNull(actual.get(0));
        List<Component> twice = adaptor.getInstancesOfType(Component.class);
        assertSame(actual.get(0), twice.get(0));
    }

    public static final class Component {
    }

    public static final class HasntDefaultConstractorCompoent {

        @SuppressWarnings("unused")
        private final String foo;

        public HasntDefaultConstractorCompoent(String foo) {
            this.foo = foo;
        }
    }

    private static final class DoNotAccessableCompoent {

        private DoNotAccessableCompoent() {
        }
    }
}
