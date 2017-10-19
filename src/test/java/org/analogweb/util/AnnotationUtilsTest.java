package org.analogweb.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

/**
 * @author snowgoose
 */
public class AnnotationUtilsTest {

	@Test
	public void testFindAnnotation() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, SomeClass.class.getAnnotations());
		assertNotNull(actual);
	}

	@Test
	public void testFindAnnotationInclude() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, SomeIncludeClass.class.getAnnotations());
		assertNotNull(actual);
	}

	@Test
	public void testFindAnnotationViaType() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, SomeClass.class);
		assertNotNull(actual);
	}

	@Test
	public void testFindAnnotationViaIncludedClass() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, SomeIncludeClass.class);
		assertNotNull(actual);
	}

	@Test
	public void testFindAnnotationViaImplementsClass() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, SomeClassImpl.class);
		assertNotNull(actual);
	}

	@Test
	public void testFindAnnotationNotFound() {
		SomeAnnotation actual = AnnotationUtils.findAnnotation(
				SomeAnnotation.class, AnyClass.class.getAnnotations());
		assertNull(actual);
	}

	@Test
	public void testFindAnnotationNotFound2() {
		AnyAnnotation actual = AnnotationUtils.findAnnotation(
				AnyAnnotation.class, AnyClass.class.getAnnotations());
		assertNull(actual);
	}

	@Test
	public void testFindAnnotationsViaMethod() throws Exception {
		Method method = SomeClass.class.getMethod("doSomething");
		List<SomeAnnotation> actual = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(instanceOf(SomeAnnotation.class)));
	}

	@Test
	public void testFindAnnotationViaExtendsMethod() throws Exception {
		Method method = SomeClassExtends.class.getMethod("doSomething");
		List<SomeAnnotation> actual = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(instanceOf(SomeAnnotation.class)));
	}

	@Test
	public void testFindAnnotationViaDelegatedMethod() throws Exception {
		Method method = SomeIncludeClass.class.getMethod("doAnything");
		List<SomeAnnotation> actual = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(instanceOf(SomeAnnotation.class)));
	}

	@Test
	public void testFindManyAnnotationViaDelegatedMethod() throws Exception {
		Method method = ManyIncludeClass.class.getMethod("doAnything");
		List<SomeAnnotation> actual = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is(instanceOf(SomeAnnotation.class)));
	}

	@Test
	public void testFindInclusiveAnnotationViaDelegatedMethod()
			throws Exception {
		Method method = ManyIncludeClass.class.getMethod("doSomething");
		List<SomeAnnotation> actual = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(instanceOf(SomeAnnotation.class)));
	}

	@Test
	public void testGetValue() throws Exception {
		Method method = ManyIncludeClass.class.getMethod("doAnything");
		List<SomeAnnotation> list = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		String actual = AnnotationUtils.getValue(list.get(1));
		assertThat(actual, is("hoge"));
	}

	@Test
	public void testGetValuesNotAvairableAttribute() throws Exception {
		Method method = ManyIncludeClass.class.getMethod("doAnything");
		List<SomeAnnotation> list = AnnotationUtils.findAnnotations(
				SomeAnnotation.class, method);
		String actual = AnnotationUtils.getValue(list.get(1), "hoge");
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void testIsDecleared() throws Exception {
		boolean actual = AnnotationUtils.isDeclared(SomeAnnotation.class,
				SomeAnnotationInclude.class);
		assertThat(actual, is(true));
		actual = AnnotationUtils.isDeclared(SomeAnnotation.class,
				SomeClass.class);
		assertThat(actual, is(true));
		actual = AnnotationUtils.isDeclared(SomeAnnotation.class,
				SomeAnnotation.class);
		assertThat(actual, is(false));
		actual = AnnotationUtils.isDeclared(SomeAnnotation.class,
				Documented.class);
		assertThat(actual, is(false));
		actual = AnnotationUtils.isDeclared(SomeAnnotationInclude.class,
				SomeClass.class);
		assertThat(actual, is(false));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
	private @interface SomeAnnotation {

		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@SomeAnnotation
	private @interface SomeAnnotationInclude {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@SomeAnnotationInclude
	private @interface SomeAnnotationReInclude {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
	private @interface AnyAnnotation {
	}

	@SomeAnnotation
	private class SomeClass {

		@SomeAnnotation
		public void doSomething() {
			// nop
		}
	}

	@SomeAnnotation
	private interface SomeClassIf {
	}

	private final class SomeClassImpl implements SomeClassIf {
	}

	@SomeAnnotationInclude
	private final class SomeIncludeClass {

		@SomeAnnotationInclude
		public void doAnything() {
			// nop
		}
	}

	private final class AnyClass {
	}

	private final class SomeClassExtends extends SomeClass {
	}

	private final class ManyIncludeClass {

		@SomeAnnotationInclude
		@SomeAnnotation("hoge")
		public void doAnything() {
			// nop
		}

		@SomeAnnotationReInclude
		public void doSomething() {
			// nop
		}
	}
}
