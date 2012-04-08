package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.analogweb.core.StaticMappingContainerAdaptor;
import org.analogweb.core.StaticMappingContainerAdaptor.AssignableFromClassKey;
import org.analogweb.exception.AssertionFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorTest {

    private StaticMappingContainerAdaptor adaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        adaptor = new StaticMappingContainerAdaptor();
    }

    @Test
    public void testGetInstanceOfType() {
        adaptor.register(MockInterface.class, BaaObject.class);
        MockInterface instance = adaptor.getInstanceOfType(MockInterface.class);
        assertTrue(instance instanceof BaaObject);
    }

    @Test
    public void testGetInstanceOfDuplicateType() {
        adaptor.register(MockInterface.class, BaaObject.class);
        adaptor.register(MockInterface.class, FooObject.class);
        MockInterface instance = adaptor.getInstanceOfType(MockInterface.class);
        assertTrue(instance instanceof BaaObject || instance instanceof FooObject);
    }

    @Test
    public void testGetInstanceOfTypeWithNoResult() {
        MockInterface instance = adaptor.getInstanceOfType(MockInterface.class);
        assertNull(instance);
    }

    @Test
    public void testGetInstancesOfType() {
        adaptor.register(MockInterface.class, FooObject.class);
        adaptor.register(MockInterface.class, BaaObject.class);
        List<MockInterface> instance = adaptor.getInstancesOfType(MockInterface.class);
        assertThat(instance.size(), is(2));
    }

    @Test
    public void testGetInstancesOfTypeWithNoResult() {
        List<MockInterface> instance = adaptor.getInstancesOfType(MockInterface.class);
        assertTrue(instance.isEmpty());
    }

    @Test
    public void testGetInstancesOfConcleteType() {
        adaptor.register(FooObject.class);
        adaptor.register(BaaObject.class);
        List<MockInterface> instance = adaptor.getInstancesOfType(MockInterface.class);
        assertThat(instance.size(), is(2));
    }

    @Test
    public void testGetInstancesOfSupreype() {
        adaptor.register(BazChildObject.class);
        adaptor.register(BaaObject.class);
        List<MockInterface> instance = adaptor.getInstancesOfType(MockInterface.class);
        assertThat(instance.size(), is(2));
    }

    @Test
    public void testRegisterWithNullArg() {
        thrown.expect(AssertionFailureException.class);
        adaptor.register(null);
    }

    @Test
    public void testGetInstancesWithConcleteClass() {
        adaptor.register(BazChildObject.class);
        BazChildObject instance = adaptor.getInstanceOfType(BazChildObject.class);
        assertNotNull(instance);
    }

    @Test
    public void testRegisterWithNullConcleteArg() {
        thrown.expect(AssertionFailureException.class);
        adaptor.register(MockInterface.class, null);
    }

    @Test
    public void testRegisterWithNullRequiredTypeArg() {
        thrown.expect(AssertionFailureException.class);
        adaptor.register(null, FooObject.class);
    }

    @Test
    public void testSpecOfAssignableFromClassKey() {
        AssignableFromClassKey key = AssignableFromClassKey.valueOf(Collection.class);
        assertTrue(key.equals(AssignableFromClassKey.valueOf(List.class)));
        assertTrue(key.equals(AssignableFromClassKey.valueOf(Set.class)));
        assertFalse(key.equals(AssignableFromClassKey.valueOf(Map.class)));
        assertFalse(key.equals(List.class));
        assertFalse(key.equals(null));
    }

    interface MockInterface {

    }

    public static class FooObject implements MockInterface {

    }

    public static class BaaObject implements MockInterface {

    }

    public static abstract class BazObject implements MockInterface {
    }

    public static class BazChildObject extends BazObject {

    }
}
