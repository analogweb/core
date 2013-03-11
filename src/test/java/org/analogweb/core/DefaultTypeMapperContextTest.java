package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.analogweb.ContainerAdaptor;
import org.analogweb.TypeMapper;
import org.analogweb.core.AssertionFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.stubbing.OngoingStubbing;

/**
 * @author snowgoose
 */
public class DefaultTypeMapperContextTest {

	private DefaultTypeMapperContext context;
	private TypeMapper typeMapper;
	private TypeMapper defaultTypeMapper;
	private ContainerAdaptor containerAdaptor;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		containerAdaptor = mock(ContainerAdaptor.class);
		context = new DefaultTypeMapperContext();
		context.setModulesContainerAdaptor(containerAdaptor);
		typeMapper = mock(TypeMapper.class);
		defaultTypeMapper = mock(TypeMapper.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMapToType() {
		String[] formats = new String[] { "yyyy/MM/dd" };
		Date now = new Date();
		((OngoingStubbing<TypeMapper>) when(containerAdaptor
				.getInstanceOfType(typeMapper.getClass())))
				.thenReturn(typeMapper);
		when(typeMapper.mapToType("2010/08/10", Date.class, formats))
				.thenReturn(now);
		Date actual = context.mapToType(typeMapper.getClass(), "2010/08/10",
				Date.class, formats);
		assertThat(actual, is(now));
	}

	@Test
	public void testMapToTypeSameType() {
		String actual = context.mapToType(null, "2010/08/10", String.class,
				null);
		assertThat(actual, is("2010/08/10"));
	}

	@Test
	public void testMapToTypeMapperNotFound() {
		String[] formats = new String[] { "yyyy/MM/dd" };
		Date now = new Date();
		// not found.
		when(containerAdaptor.getInstanceOfType(typeMapper.getClass()))
				.thenReturn(null);
		when(defaultTypeMapper.mapToType("2010/08/10", Date.class, formats))
				.thenReturn(now);

		context.setDefaultTypeMapper(defaultTypeMapper);
		Date actual = context.mapToType(typeMapper.getClass(), "2010/08/10",
				Date.class, formats);
		assertThat(actual, is(now));
	}

	@Test
	public void testMapToTypeMapperWithNullTypeMapper() {
		String[] formats = new String[] { "yyyy/MM/dd" };
		Date now = new Date();
		// not found.
		when(defaultTypeMapper.mapToType("2010/08/10", Date.class, formats))
				.thenReturn(now);

		context.setDefaultTypeMapper(defaultTypeMapper);
		Date actual = context
				.mapToType(null, "2010/08/10", Date.class, formats);
		assertThat(actual, is(now));
	}

	@Test
	public void testMapToTypeMapperWithDefaultTypeMapper() {
		String[] formats = new String[] { "yyyy/MM/dd" };
		Date now = new Date();
		// not found.
		when(defaultTypeMapper.mapToType("2010/08/10", Date.class, formats))
				.thenReturn(now);

		context.setDefaultTypeMapper(defaultTypeMapper);
		Date actual = context.mapToType(TypeMapper.class, "2010/08/10",
				Date.class, formats);
		assertThat(actual, is(now));
	}

	@Test
	public void testMapToTypeMapperWithNullReqiredType() {
		thrown.expect(AssertionFailureException.class);
		String[] formats = new String[] { "yyyy/MM/dd" };
		context.mapToType(typeMapper.getClass(), "2010/08/10", null, formats);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMapToTypeMapperWithCollection() {
		Object actual = context.mapToType(typeMapper.getClass(),
				Arrays.asList("foo"), List.class, null);
		List<String> actualList = (List<String>) actual;
		assertThat(actualList.get(0), is("foo"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMapToTypeMapperWithSubTypeOfCollection() {
		Object actual = context.mapToType(typeMapper.getClass(),
				Arrays.asList(1L, 2L), List.class, null);
		List<Number> actualList = (List<Number>) actual;
		assertThat(actualList.get(0).longValue(), is(1L));
	}

	@Test
	public void testMapToTypeMapperWithSingleOfCollection() {
		String actual = context.mapToType(typeMapper.getClass(),
				Arrays.asList("foo"), String.class, null);
		assertThat(actual, is("foo"));
	}

	@Test
	public void testMapToTypeMapperWithSubTypeSingleOfCollection() {
		Number actual = context.mapToType(typeMapper.getClass(),
				Arrays.asList(1L), Number.class, null);
		assertThat(actual.longValue(), is(1L));
	}

	@Test
	public void testMapToTypeMapperWithArray() {
		String[] actualArray = context.mapToType(typeMapper.getClass(),
				new String[] { "foo", "baa" }, String[].class, null);
		assertThat(actualArray[0], is("foo"));
		assertThat(actualArray[1], is("baa"));
	}

	@Test
	public void testMapToTypeMapperWithSingleOfArray() {
		String actual = context.mapToType(typeMapper.getClass(), new String[] {
				"foo", "baa" }, String.class, null);
		assertThat(actual, is("foo"));
	}

	@Test
	public void testMapToTypeMapperWithSubTypeOfArray() {
		Number[] actualArray = context.mapToType(typeMapper.getClass(),
				new Long[] { 1L, 2L }, Number[].class, null);
		assertThat(actualArray[0].longValue(), is(1L));
		assertThat(actualArray[1].longValue(), is(2L));
	}

	@Test
	public void testMapToTypeMapperWithSingleSubTypeOfArray() {
		Number actual = context.mapToType(typeMapper.getClass(), new Integer[] {
				1, 2 }, Number.class, null);
		assertThat(actual.intValue(), is(1));
	}

}
