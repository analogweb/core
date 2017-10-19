package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * test case for {@link ReflectionUtils}
 * 
 * @author snowgoose
 */
public class ReflectionUtilsTest {

	@Test
	public void testGetMethodParameterAnnotation() throws Exception {
		Method doSomething = MockObject.class.getMethod("doSomething",
				new Class<?>[]{String.class});
		Foo foo = ReflectionUtils.getMethodParameterAnnotation(doSomething,
				Foo.class, 0);
		assertNotNull(foo);
	}

	@Test
	public void testGetMethodParameterAnnotationOutOfIndex() throws Exception {
		Method doSomething = MockObject.class.getMethod("doSomething",
				new Class<?>[]{String.class});
		Foo foo = ReflectionUtils.getMethodParameterAnnotation(doSomething,
				Foo.class, 1);
		assertNull(foo);
	}

	@Test
	public void testGetInstanceQuietly() {
		MockObject actual = ReflectionUtils
				.getInstanceQuietly(MockObject.class);
		assertNotNull(actual);
	}

	@Test
	public void testGetInstanceQuietlyWithNotMatchConstractorArgs() {
		MockObjectWithConstractorArg actual = ReflectionUtils
				.getInstanceQuietly(MockObjectWithConstractorArg.class);
		assertNull(actual);
	}

	@Test
	public void testGetInstanceQuietlyWithPrivateConstractorArgs() {
		Object actual = ReflectionUtils.getInstanceQuietly(
				MockObjectWithConstractorArg.class,
				MockObjectWithConstractorArg.class.getConstructors()[0], "foo");
		assertNotNull(actual);
		actual = ReflectionUtils.getInstanceQuietly(
				MockObjectWithConstractorArg.class,
				MockObjectWithConstractorArg.class.getConstructors()[0],
				(Object) null);
		assertNotNull(actual);
		actual = ReflectionUtils.getInstanceQuietly(
				MockObjectWithConstractorArg.class,
				MockObjectWithConstractorArg.class.getConstructors()[0],
				new Date());
		assertNull(actual);
		actual = ReflectionUtils.getInstanceQuietly(MockIIObjectAbstract.class,
				MockIIObjectAbstract.class.getConstructors()[0], new Date());
		assertNull(actual);
		actual = ReflectionUtils.getInstanceQuietly(
				MockObjectWithConstractorArg.class, null, "foo");
		assertNull(actual);
	}

	@Test
	public void testGetInstanceQuietlyWithPrivateConstractor() {
		MockObjectWithPrivateConstractor actual = ReflectionUtils
				.getInstanceQuietly(MockObjectWithPrivateConstractor.class);
		assertNull(actual);
	}

	@Test
	public void testFilterClassAsImplementsInterface() {
		Collection<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(MockIIObject.class);
		classes.add(MockIIObjectExt.class);
		classes.add(MockObject.class);
		List<Class<IIMockObject>> result = ReflectionUtils
				.filterClassAsImplementsInterface(IIMockObject.class, classes);
		assertThat(result.size(), is(2));
		assertTrue(result.contains(MockIIObject.class));
		assertTrue(result.contains(MockIIObjectExt.class));
	}

	@Test
	public void testWriteValueToPrivateField() {
		MockObject obj = new MockObject();
		ReflectionUtils.writeValueToField("hoge", obj, "fuga");
		assertThat(obj.hoge, is("fuga"));
	}

	@Test
	public void testWriteValueToNotAvailableField() {
		MockObject obj = new MockObject();
		ReflectionUtils.writeValueToField("noexists", obj, "fuga");
		assertNull(obj.hoge);
	}

	@Test
	public void testGetValueViaPrivateField() {
		MockObject obj = new MockObject();
		obj.hoge = "foo";
		Object actual = ReflectionUtils.getValueOfField("hoge",
				Modifier.PRIVATE, obj);
		assertThat(actual.toString(), is("foo"));
	}

	@Test
	public void testGetValueViaNotAvailableField() {
		MockObject obj = new MockObject();
		obj.hoge = "foo";
		Object actual = ReflectionUtils.getValueOfField("noexists",
				Modifier.PRIVATE, obj);
		assertNull(actual);
	}

	@Test
	public void testGetDeclaredMethodQuietly() {
		Method doSomething = ReflectionUtils.getMethodQuietly(MockObject.class,
				"doSomething", new Class[]{String.class});
		assertNotNull(doSomething);
	}

	@Test
	public void testGetDeclaredMethodQuietlyNoSuchMethod() {
		Method doSomething = ReflectionUtils.getMethodQuietly(MockObject.class,
				"doAnything", new Class[]{String.class});
		assertNull(doSomething);
	}

	@Test
	public void testFindAllImplementsInterfacesRecursivery() {
		Set<Class<?>> actual = ReflectionUtils
				.findAllImplementsInterfacesRecursivery(MockIIObjectExt.class);
		assertThat(actual.size(), is(2));
		assertTrue(actual.contains(IMockObject.class));
		assertTrue(actual.contains(IIMockObject.class));
	}

	@Test
	public void testFindAllImplementsInterfacesRecursiveryWithObject() {
		Set<Class<?>> actual = ReflectionUtils
				.findAllImplementsInterfacesRecursivery(Object.class);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void testFindAllImplementsInterfacesRecursiveryWithNull() {
		Set<Class<?>> actual = ReflectionUtils
				.findAllImplementsInterfacesRecursivery(null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void testGetCallerClass() {
		List<Class<?>> actual = new MockObject().getCallerClasses();
		assertThat(actual.get(0).getCanonicalName(),
				is("org.analogweb.util.ReflectionUtilsTest.MockObject"));
	}

	public interface IMockObject {
	}

	public interface IIMockObject extends IMockObject {
	}

	public static class MockObject implements IMockObject {

		private String hoge;

		public String doSomething(@Foo String foo) {
			return "doSomething!" + foo;
		}

		public String doSomethingWithException(@Foo String foo) {
			throw new RuntimeException();
		}

		public List<Class<?>> getCallerClasses() {
			return ReflectionUtils.getCallerClasses();
		}

	}

	public static class MockIIObject implements IIMockObject {
	}

	public static class MockIIObjectExt extends MockIIObject {
	}

	public static abstract class MockIIObjectAbstract extends MockIIObject {
	}

	public static class MockObjectWithConstractorArg extends MockObject {

		public MockObjectWithConstractorArg(String boo) {
			// nop.
		}
	}

	public static class MockObjectWithPrivateConstractor extends MockObject {

		private MockObjectWithPrivateConstractor(String boo) {
			// nop.
		}
	}

	@Target({ElementType.PARAMETER, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface Foo {
	}
}
