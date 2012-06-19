package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.analogweb.ContainerAdaptor;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;
import org.analogweb.exception.AssertionFailureException;
import org.junit.After;
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
    private RequestAttributes attributes;
    private RequestContext requestContext;
    private ContainerAdaptor containerAdaptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        containerAdaptor = mock(ContainerAdaptor.class);
        context = new DefaultTypeMapperContext(containerAdaptor);
        typeMapper = mock(TypeMapper.class);
        defaultTypeMapper = mock(TypeMapper.class);
        attributes = mock(RequestAttributes.class);
        requestContext = mock(RequestContext.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapToType() {
        String[] formats = new String[] { "yyyy/MM/dd" };
        Date now = new Date();
        ((OngoingStubbing<TypeMapper>) when(containerAdaptor.getInstanceOfType(typeMapper
                .getClass()))).thenReturn(typeMapper);
        when(typeMapper.mapToType(requestContext, attributes, "2010/08/10", Date.class, formats))
                .thenReturn(now);
        Date actual = (Date) context.mapToType(typeMapper.getClass(), requestContext, attributes,
                "2010/08/10", Date.class, formats);
        assertThat(actual, is(now));
    }

    @Test
    public void testMapToTypeSameType() {
        String actual = (String) context.mapToType(null, requestContext, attributes, "2010/08/10",
                String.class, null);
        assertThat(actual, is("2010/08/10"));
    }

    @Test
    public void testMapToTypeMapperNotFound() {
        String[] formats = new String[] { "yyyy/MM/dd" };
        Date now = new Date();
        // not found.
        when(containerAdaptor.getInstanceOfType(typeMapper.getClass())).thenReturn(null);
        when(
                defaultTypeMapper.mapToType(requestContext, attributes, "2010/08/10", Date.class,
                        formats)).thenReturn(now);

        context.setDefaultTypeMapper(defaultTypeMapper);
        Date actual = (Date) context.mapToType(typeMapper.getClass(), requestContext, attributes,
                "2010/08/10", Date.class, formats);
        assertThat(actual, is(now));
    }

    @Test
    public void testMapToTypeMapperWithNullTypeMapper() {
        String[] formats = new String[] { "yyyy/MM/dd" };
        Date now = new Date();
        // not found.
        when(
                defaultTypeMapper.mapToType(requestContext, attributes, "2010/08/10", Date.class,
                        formats)).thenReturn(now);

        context.setDefaultTypeMapper(defaultTypeMapper);
        Date actual = (Date) context.mapToType(null, requestContext, attributes, "2010/08/10",
                Date.class, formats);
        assertThat(actual, is(now));
    }

    @Test
    public void testMapToTypeMapperWithDefaultTypeMapper() {
        String[] formats = new String[] { "yyyy/MM/dd" };
        Date now = new Date();
        // not found.
        when(
                defaultTypeMapper.mapToType(requestContext, attributes, "2010/08/10", Date.class,
                        formats)).thenReturn(now);

        context.setDefaultTypeMapper(defaultTypeMapper);
        Date actual = (Date) context.mapToType(TypeMapper.class, requestContext, attributes,
                "2010/08/10", Date.class, formats);
        assertThat(actual, is(now));
    }

    @Test
    public void testMapToTypeMapperWithNullRequestAttribute() {
        thrown.expect(AssertionFailureException.class);
        String[] formats = new String[] { "yyyy/MM/dd" };
        context.mapToType(typeMapper.getClass(), requestContext, null, "2010/08/10", Date.class,
                formats);
    }

    @Test
    public void testMapToTypeMapperWithNullReqiredType() {
        thrown.expect(AssertionFailureException.class);
        String[] formats = new String[] { "yyyy/MM/dd" };
        context.mapToType(typeMapper.getClass(), requestContext, attributes, "2010/08/10", null,
                formats);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapToTypeMapperWithCollection() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                Arrays.asList("foo"), List.class, null);
        List<String> actualList = (List<String>) actual;
        assertThat(actualList.get(0), is("foo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapToTypeMapperWithSubTypeOfCollection() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                Arrays.asList(1L,2L), List.class, null);
        List<Number> actualList = (List<Number>) actual;
        assertThat(actualList.get(0).longValue(), is(1L));
    }

    @Test
    public void testMapToTypeMapperWithSingleOfCollection() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                Arrays.asList("foo"), String.class, null);
        assertThat((String) actual, is("foo"));
    }

    @Test
    public void testMapToTypeMapperWithSubTypeSingleOfCollection() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                Arrays.asList(1L), Number.class, null);
        assertThat(((Number) actual).longValue(), is(1L));
    }

    @Test
    public void testMapToTypeMapperWithArray() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                new String[]{"foo","baa"}, String[].class, null);
        String[] actualArray = (String[]) actual;
        assertThat(actualArray[0], is("foo"));
        assertThat(actualArray[1], is("baa"));
    }

    @Test
    public void testMapToTypeMapperWithSingleOfArray() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                new String[]{"foo","baa"}, String.class, null);
        assertThat((String) actual, is("foo"));
    }

    @Test
    public void testMapToTypeMapperWithSubTypeOfArray() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                new Long[]{1L,2L}, Number[].class, null);
        Number[] actualArray = (Number[]) actual;
        assertThat(actualArray[0].longValue(), is(1L));
        assertThat(actualArray[1].longValue(), is(2L));
    }

    @Test
    public void testMapToTypeMapperWithSingleSubTypeOfArray() {
        Object actual = context.mapToType(typeMapper.getClass(), requestContext, attributes,
                new Integer[]{1,2}, Number.class, null);
        assertThat(((Number) actual).intValue(), is(1));
    }

}
