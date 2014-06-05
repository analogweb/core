package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import org.analogweb.InvocationMetadata;
import org.analogweb.Modules;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.Bean;
import org.analogweb.annotation.Param;
import org.analogweb.annotation.Resolver;
import org.analogweb.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class BeanAttributeArgumentPreparatorTest {

	private BeanAttributeValueResolver preparator;
	private TypeMapperContext converters = mock(TypeMapperContext.class);
	private RequestValueResolvers resolvers = mock(RequestValueResolvers.class);
	private Modules modules;

	@Before
	public void setUp() {
		converters = mock(TypeMapperContext.class);
		resolvers = mock(RequestValueResolvers.class);
		modules = mock(Modules.class);
		when(modules.getTypeMapperContext()).thenReturn(converters);
		when(modules.getRequestValueResolvers()).thenReturn(resolvers);
	}

	@Test
	public void test() throws Exception {
		preparator = new BeanAttributeValueResolver();
		preparator.setModules(modules);
		InvocationMetadata metadata = mock(InvocationMetadata.class);
		when(metadata.getArgumentTypes()).thenReturn(
				new Class[] { SomeBean.class, String.class });
		RequestContext context = mock(RequestContext.class);
		RequestValueResolver resolver = mock(RequestValueResolver.class);
		when(resolvers.findRequestValueResolver(ParameterValueResolver.class))
				.thenReturn(resolver);
		Annotation[] anns = SomeBean.class.getDeclaredField("name")
				.getAnnotations();
		when(
				resolver.resolveValue(context, metadata, "name", String.class,
						anns)).thenReturn("Name");
		when(
				resolver.resolveValue(context, metadata, "birthDay",
						Date.class, anns)).thenReturn("2013-01-01");
		when(
				resolver.resolveValue(context, metadata, "number",
						Integer.class, anns)).thenReturn("5");
		Date expectedDate = new Date();
		when(
				converters.mapToType(eq(TypeMapper.class), eq("Name"),
						eq(String.class), isA(String[].class))).thenReturn(
				"Name");
		when(
				converters.mapToType(eq(TypeMapper.class), eq("2013-01-01"),
						eq(Date.class), isA(String[].class))).thenReturn(
				expectedDate);
		when(
				converters.mapToType(eq(TypeMapper.class), eq("5"),
						eq(Integer.class), isA(String[].class))).thenReturn(5);
		Method method = Resource.class.getDeclaredMethod("doSomething",
				SomeBean.class, String.class);
		SomeBean actual = (SomeBean) preparator.resolveValue(context, metadata,
				"", SomeBean.class, method.getAnnotations());
		assertThat(actual.getName(), is("Name"));
		assertThat(actual.getBirthDay(), is(expectedDate));
		assertThat(actual.getNumber(), is(5));
	}

	@Test
	public void testWithResolver() throws Exception {
		preparator = new BeanAttributeValueResolver();
		preparator.setModules(modules);
		InvocationMetadata metadata = mock(InvocationMetadata.class);
		when(metadata.getArgumentTypes()).thenReturn(
				new Class[] { SomeBean.class, String.class });
		RequestContext context = mock(RequestContext.class);
		RequestValueResolver resolver = mock(RequestValueResolver.class);
		RequestValueResolver beanResolver = mock(RequestValueResolver.class);
		when(resolvers.findRequestValueResolver(ParameterValueResolver.class))
				.thenReturn(resolver);
		when(resolvers.findRequestValueResolver(SomeBeanResolver.class))
				.thenReturn(beanResolver);
		SomeBean resolvedBean = new SomeBean();
		Annotation[] anns = SomeBean.class.getDeclaredField("name")
				.getAnnotations();
		when(
				beanResolver.resolveValue(context, metadata, StringUtils.EMPTY,
						SomeBean.class, anns)).thenReturn(resolvedBean);
		when(
				resolver.resolveValue(context, metadata, "name", String.class,
						anns)).thenReturn("Name");
		when(
				resolver.resolveValue(context, metadata, "birthDay",
						Date.class, anns)).thenReturn("2013-01-01");
		when(
				resolver.resolveValue(context, metadata, "number",
						Integer.class, anns)).thenReturn("5");
		Date expectedDate = new Date();
		when(
				converters.mapToType(eq(TypeMapper.class), eq("Name"),
						eq(String.class), isA(String[].class))).thenReturn(
				"Name");
		when(
				converters.mapToType(eq(TypeMapper.class), eq("2013-01-01"),
						eq(Date.class), isA(String[].class))).thenReturn(
				expectedDate);
		when(
				converters.mapToType(eq(TypeMapper.class), eq("5"),
						eq(Integer.class), isA(String[].class))).thenReturn(5);
		Method m = Resource.class.getDeclaredMethod("doAnything",
				SomeBean.class, String.class);
		SomeBean actual = (SomeBean) preparator.resolveValue(context, metadata,
				"", SomeBean.class, m.getAnnotations());
		assertThat(actual.getName(), is("Name"));
		assertThat(actual.getBirthDay(), is(expectedDate));
		assertThat(actual.getNumber(), is(5));
	}

	public static class Resource {

		public String doSomething(@Bean SomeBean bean, @Param String comment) {
			return "something!";
		}

		public String doAnything(
				@Bean @Resolver(value = SomeBeanResolver.class) SomeBean bean,
				@Param String comment) {
			return "something!";
		}
	}

	public static class SomeBean {

		@Param
		private String name;
		@Param
		private Date birthDay;
		@Param
		private Integer number;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getBirthDay() {
			return birthDay;
		}

		// without mutator.
		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}
	}

	public static class SomeBeanResolver implements RequestValueResolver {

		@Override
		public Object resolveValue(RequestContext requestContext,
				InvocationMetadata metadata, String key, Class<?> requiredType,
				Annotation[] annotations) {
			// nop.
			return null;
		}
	}
}
