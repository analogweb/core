package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;
import org.analogweb.core.AutoTypeMapper.ClassPair;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AutoTypeMapperTest {

    private AutoTypeMapper typeMapper;
    private RequestAttributes attributes;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        typeMapper = new AutoTypeMapper();
        attributes = mock(RequestAttributes.class);
        context = mock(RequestContext.class);
    }

    @Test
    public void testMapToType() {
        String actual = (String) typeMapper.mapToType(context, attributes, "foo", String.class,
                null);
        assertThat(actual, is("foo"));
    }

    @Test
    public void testMapToTypeMapperNotFound() {
        Object actual = typeMapper.mapToType(context, attributes, "foo", TypeMapper.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToByte() {
        byte actual = (Byte) typeMapper.mapToType(context, attributes, null, byte.class, null);
        assertThat(actual, is((byte) 0));
    }

    @Test
    public void testMapToTypeToBoolean() {
        Boolean actual = (Boolean) typeMapper.mapToType(context, attributes, "true", Boolean.class,
                null);
        assertTrue(actual);
        boolean nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "true",
                boolean.class, null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType(context, attributes, "on", Boolean.class, null);
        assertTrue(actual);
        nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "on", boolean.class,
                null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType(context, attributes, "yes", Boolean.class, null);
        assertTrue(actual);
        nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "yes", boolean.class,
                null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType(context, attributes, "false", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "false", boolean.class,
                null);
        assertFalse(nativeActual);
        actual = (Boolean) typeMapper.mapToType(context, attributes, "no", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "no", boolean.class,
                null);
        assertFalse(nativeActual);
        actual = (Boolean) typeMapper.mapToType(context, attributes, "", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType(context, attributes, "", boolean.class, null);
        assertFalse(nativeActual);
        boolean nativeBool = (Boolean) typeMapper.mapToType(context, attributes, null,
                boolean.class, null);
        assertFalse(nativeBool);
    }

    @Test
    public void testMapToTypeToCharactor() throws Exception {
        Character actual = (Character) typeMapper.mapToType(context, attributes, "ab",
                Character.class, null);
        assertThat(actual, is('a'));
        actual = (Character) typeMapper.mapToType(context, attributes, "ab", char.class, null);
        assertThat(actual, is('a'));
        actual = (Character) typeMapper.mapToType(context, attributes, "b", Character.class, null);
        assertThat(actual, is('b'));
        actual = (Character) typeMapper.mapToType(context, attributes, "b", char.class, null);
        assertThat(actual, is('b'));
        actual = (Character) typeMapper.mapToType(context, attributes, "A", Character.class, null);
        assertThat(actual, is('A'));
        actual = (Character) typeMapper
                .mapToType(context, attributes, "ABC", Character.class, null);
        assertThat(actual, is('A'));
        assertNull(typeMapper.mapToType(context, attributes, "", Character.class, null));
        assertNull(typeMapper.mapToType(context, attributes, null, Character.class, null));
        char actualChar = (Character) typeMapper.mapToType(context, attributes, null, char.class,
                null);
        assertThat(actualChar, is(Character.MIN_VALUE));
    }

    @Test
    public void testMapToTypeToShort() throws Exception {
        Short actual = (Short) typeMapper.mapToType(context, attributes, "1", Short.class, null);
        assertThat(actual, is((short) 1));
        actual = (Short) typeMapper.mapToType(context, attributes, "1", short.class, null);
        assertThat(actual, is((short) 1));
        actual = (Short) typeMapper.mapToType(context, attributes, null, Short.class, null);
        assertNull(actual);
        short actualShort = (Short) typeMapper.mapToType(context, attributes, null, short.class,
                null);
        assertThat(actualShort, is((short) 0));
        actual = (Short) typeMapper.mapToType(context, attributes, "", Short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType(context, attributes, "", short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType(context, attributes, "Nan", Short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType(context, attributes, "Not-A-Number", short.class,
                null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToDouble() throws Exception {
        Double actual = (Double) typeMapper.mapToType(context, attributes, "1.0", Double.class,
                null);
        assertThat(actual, is(1.0));
        actual = (Double) typeMapper.mapToType(context, attributes, "2.2", double.class, null);
        assertThat(actual, is(2.2));
        actual = (Double) typeMapper.mapToType(context, attributes, null, Double.class, null);
        assertNull(actual);
        double actualDouble = (Double) typeMapper.mapToType(context, attributes, null,
                double.class, null);
        assertThat(actualDouble, is(0.0d));
        actual = (Double) typeMapper.mapToType(context, attributes, "", Double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType(context, attributes, "", double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType(context, attributes, "Nan", Double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType(context, attributes, "Not-A-Number", double.class,
                null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToFloat() throws Exception {
        Float actual = (Float) typeMapper.mapToType(context, attributes, "1.0", Float.class, null);
        assertThat(actual, is(1.0f));
        actual = (Float) typeMapper.mapToType(context, attributes, "2.2", float.class, null);
        assertThat(actual, is(2.2f));
        actual = (Float) typeMapper.mapToType(context, attributes, null, Float.class, null);
        assertNull(actual);
        float actualFloat = (Float) typeMapper.mapToType(context, attributes, null, float.class,
                null);
        assertThat(actualFloat, is(0.0f));
        actual = (Float) typeMapper.mapToType(context, attributes, "", Float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType(context, attributes, "", float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType(context, attributes, "Nan", Float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType(context, attributes, "Not-A-Number", float.class,
                null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToInteger() throws Exception {
        Integer actual = (Integer) typeMapper.mapToType(context, attributes, "1", Integer.class,
                null);
        assertThat(actual, is(1));
        actual = (Integer) typeMapper.mapToType(context, attributes, "1", int.class, null);
        assertThat(actual, is(1));
        actual = (Integer) typeMapper.mapToType(context, attributes, null, Integer.class, null);
        assertNull(actual);
        int actualInt = (Integer) typeMapper.mapToType(context, attributes, null, int.class, null);
        assertThat(actualInt, is(0));
        actual = (Integer) typeMapper.mapToType(context, attributes, "", Integer.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType(context, attributes, "", int.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType(context, attributes, "Nan", Integer.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType(context, attributes, "Not-A-Number", int.class,
                null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToLong() throws Exception {
        Long actual = (Long) typeMapper.mapToType(context, attributes, "55590", Long.class, null);
        assertThat(actual, is(55590L));
        actual = (Long) typeMapper.mapToType(context, attributes, "1000", long.class, null);
        assertThat(actual, is(1000L));
        actual = (Long) typeMapper.mapToType(context, attributes, null, Long.class, null);
        assertNull(actual);
        long actualLong = (Long) typeMapper.mapToType(context, attributes, null, long.class, null);
        assertThat(actualLong, is(0L));
        actual = (Long) typeMapper.mapToType(context, attributes, "", Long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType(context, attributes, "", long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType(context, attributes, "Nan", Long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType(context, attributes, "Not-A-Number", long.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToDate() throws Exception {
        Date expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/09");
        Date actual = (Date) typeMapper.mapToType(context, attributes, "2010-12-09", Date.class,
                null);
        assertThat(actual, is(expected));

        actual = (Date) typeMapper.mapToType(context, attributes, "2010-12-09", Date.class,
                new String[0]);
        assertThat(actual, is(expected));

        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/10");
        actual = (Date) typeMapper.mapToType(context, attributes, "2010/12/10", Date.class, null);
        assertThat(actual, is(expected));

        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/10");
        actual = (Date) typeMapper.mapToType(context, attributes, "2010_12_10", Date.class, null);
        assertNull(actual);

        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/11");
        actual = (Date) typeMapper.mapToType(context, attributes, "2010/12/11", Date.class,
                new String[] { "yyyy/MM/dd" });
        assertThat(actual, is(expected));

        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/11");
        actual = (Date) typeMapper.mapToType(context, attributes, "2010/12/11", Date.class,
                new String[] { "yyyy-MM-dd", "yyyy=MM=dd" });
        assertNull(actual);

    }

    @Test
    public void testMapToTypeToBigDecimal() throws Exception {
        BigDecimal actual = (BigDecimal) typeMapper.mapToType(context, attributes, "1001.1",
                BigDecimal.class, null);
        assertThat(actual, is(new BigDecimal("1001.1")));

        actual = (BigDecimal) typeMapper.mapToType(context, attributes, "1001.1", BigDecimal.class,
                new String[0]);
        assertThat(actual, is(new BigDecimal("1001.1")));

        actual = (BigDecimal) typeMapper.mapToType(context, attributes, "57,311,001.11",
                BigDecimal.class, new String[] { "#,###,###" });
        assertThat(actual, is(new BigDecimal("57311001.11")));

        actual = (BigDecimal) typeMapper.mapToType(context, attributes, "57,311,001.11",
                BigDecimal.class, new String[] { "invalid-format" });
        assertThat(actual,is(nullValue()));
    }
    
    @Test
    public void testMapToTypeArrayToString() throws Exception {
        String actual = (String) typeMapper.mapToType(context, attributes, new String[]{"a"},
                String.class, null);
        assertThat(actual,is("a"));

        actual = (String) typeMapper.mapToType(context, attributes, new String[]{"a","b"},
                String.class, null);
        assertThat(actual,is("a"));

        actual = (String) typeMapper.mapToType(context, attributes, new String[0],
                String.class, null);
        assertThat(actual,is(nullValue()));
    }

    @Test
    public void testClassPair() {
        ClassPair pair1 = ClassPair.valueOf(String.class, Integer.class);
        ClassPair pair2 = ClassPair.valueOf(String.class, Long.class);
        ClassPair pair3 = ClassPair.valueOf(String.class, int.class);
        ClassPair pair4 = ClassPair.valueOf(Integer.class, String.class);
        ClassPair pair5 = ClassPair.valueOf(String.class, Integer.class);

        assertFalse(pair1.equals(pair2));
        assertFalse(pair1.equals(pair3));
        assertFalse(pair1.equals(pair4));
        assertFalse(pair1.equals(new Object()));

        assertTrue(pair1.equals(pair5));
    }

}
