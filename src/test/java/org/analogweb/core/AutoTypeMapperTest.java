package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.analogweb.TypeMapper;
import org.analogweb.core.AutoTypeMapper.ClassPair;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AutoTypeMapperTest {

    private AutoTypeMapper typeMapper;

    @Before
    public void setUp() throws Exception {
        typeMapper = new AutoTypeMapper();
    }

    @Test
    public void testMapToType() {
        String actual = (String) typeMapper.mapToType("foo", String.class, null);
        assertThat(actual, is("foo"));
    }

    @Test
    public void testMapToTypeMapperNotFound() {
        Object actual = typeMapper.mapToType("foo", TypeMapper.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToByte() {
        byte actual = (Byte) typeMapper.mapToType(null, byte.class, null);
        assertThat(actual, is((byte) 0));
    }

    @Test
    public void testMapToTypeToBoolean() {
        Boolean actual = (Boolean) typeMapper.mapToType("true", Boolean.class, null);
        assertTrue(actual);
        boolean nativeActual = (Boolean) typeMapper.mapToType("true", boolean.class, null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType("on", Boolean.class, null);
        assertTrue(actual);
        nativeActual = (Boolean) typeMapper.mapToType("on", boolean.class, null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType("yes", Boolean.class, null);
        assertTrue(actual);
        nativeActual = (Boolean) typeMapper.mapToType("yes", boolean.class, null);
        assertTrue(nativeActual);
        actual = (Boolean) typeMapper.mapToType("false", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType("false", boolean.class, null);
        assertFalse(nativeActual);
        actual = (Boolean) typeMapper.mapToType("no", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType("no", boolean.class, null);
        assertFalse(nativeActual);
        actual = (Boolean) typeMapper.mapToType("", Boolean.class, null);
        assertFalse(actual);
        nativeActual = (Boolean) typeMapper.mapToType("", boolean.class, null);
        assertFalse(nativeActual);
        boolean nativeBool = (Boolean) typeMapper.mapToType(null, boolean.class, null);
        assertFalse(nativeBool);
    }

    @Test
    public void testMapToTypeToCharactor() throws Exception {
        Character actual = (Character) typeMapper.mapToType("ab", Character.class, null);
        assertThat(actual, is('a'));
        actual = (Character) typeMapper.mapToType("ab", char.class, null);
        assertThat(actual, is('a'));
        actual = (Character) typeMapper.mapToType("b", Character.class, null);
        assertThat(actual, is('b'));
        actual = (Character) typeMapper.mapToType("b", char.class, null);
        assertThat(actual, is('b'));
        actual = (Character) typeMapper.mapToType("A", Character.class, null);
        assertThat(actual, is('A'));
        actual = (Character) typeMapper.mapToType("ABC", Character.class, null);
        assertThat(actual, is('A'));
        assertNull(typeMapper.mapToType("", Character.class, null));
        assertNull(typeMapper.mapToType(null, Character.class, null));
        char actualChar = (Character) typeMapper.mapToType(null, char.class, null);
        assertThat(actualChar, is(Character.MIN_VALUE));
    }

    @Test
    public void testMapToTypeToShort() throws Exception {
        Short actual = (Short) typeMapper.mapToType("1", Short.class, null);
        assertThat(actual, is((short) 1));
        actual = (Short) typeMapper.mapToType("1", short.class, null);
        assertThat(actual, is((short) 1));
        actual = (Short) typeMapper.mapToType(null, Short.class, null);
        assertNull(actual);
        short actualShort = (Short) typeMapper.mapToType(null, short.class, null);
        assertThat(actualShort, is((short) 0));
        actual = (Short) typeMapper.mapToType("", Short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType("", short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType("Nan", Short.class, null);
        assertNull(actual);
        actual = (Short) typeMapper.mapToType("Not-A-Number", short.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToDouble() throws Exception {
        Double actual = (Double) typeMapper.mapToType("1.0", Double.class, null);
        assertThat(actual, is(1.0));
        actual = (Double) typeMapper.mapToType("2.2", double.class, null);
        assertThat(actual, is(2.2));
        actual = (Double) typeMapper.mapToType(null, Double.class, null);
        assertNull(actual);
        double actualDouble = (Double) typeMapper.mapToType(null, double.class, null);
        assertThat(actualDouble, is(0.0d));
        actual = (Double) typeMapper.mapToType("", Double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType("", double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType("Nan", Double.class, null);
        assertNull(actual);
        actual = (Double) typeMapper.mapToType("Not-A-Number", double.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToFloat() throws Exception {
        Float actual = (Float) typeMapper.mapToType("1.0", Float.class, null);
        assertThat(actual, is(1.0f));
        actual = (Float) typeMapper.mapToType("2.2", float.class, null);
        assertThat(actual, is(2.2f));
        actual = (Float) typeMapper.mapToType(null, Float.class, null);
        assertNull(actual);
        float actualFloat = (Float) typeMapper.mapToType(null, float.class, null);
        assertThat(actualFloat, is(0.0f));
        actual = (Float) typeMapper.mapToType("", Float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType("", float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType("Nan", Float.class, null);
        assertNull(actual);
        actual = (Float) typeMapper.mapToType("Not-A-Number", float.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToInteger() throws Exception {
        Integer actual = (Integer) typeMapper.mapToType("1", Integer.class, null);
        assertThat(actual, is(1));
        actual = (Integer) typeMapper.mapToType("1", int.class, null);
        assertThat(actual, is(1));
        actual = (Integer) typeMapper.mapToType(null, Integer.class, null);
        assertNull(actual);
        int actualInt = (Integer) typeMapper.mapToType(null, int.class, null);
        assertThat(actualInt, is(0));
        actual = (Integer) typeMapper.mapToType("", Integer.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType("", int.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType("Nan", Integer.class, null);
        assertNull(actual);
        actual = (Integer) typeMapper.mapToType("Not-A-Number", int.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToLong() throws Exception {
        Long actual = (Long) typeMapper.mapToType("55590", Long.class, null);
        assertThat(actual, is(55590L));
        actual = (Long) typeMapper.mapToType("1000", long.class, null);
        assertThat(actual, is(1000L));
        actual = (Long) typeMapper.mapToType(null, Long.class, null);
        assertNull(actual);
        long actualLong = (Long) typeMapper.mapToType(null, long.class, null);
        assertThat(actualLong, is(0L));
        actual = (Long) typeMapper.mapToType("", Long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType("", long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType("Nan", Long.class, null);
        assertNull(actual);
        actual = (Long) typeMapper.mapToType("Not-A-Number", long.class, null);
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToDate() throws Exception {
        Date expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/09");
        Date actual = (Date) typeMapper.mapToType("2010-12-09", Date.class, null);
        assertThat(actual, is(expected));
        actual = (Date) typeMapper.mapToType("2010-12-09", Date.class, new String[0]);
        assertThat(actual, is(expected));
        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/10");
        actual = (Date) typeMapper.mapToType("2010/12/10", Date.class, null);
        assertThat(actual, is(expected));
        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/10");
        actual = (Date) typeMapper.mapToType("2010_12_10", Date.class, null);
        assertNull(actual);
        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/11");
        actual = (Date) typeMapper.mapToType("2010/12/11", Date.class, new String[] { "yyyy/MM/dd" });
        assertThat(actual, is(expected));
        expected = new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/11");
        actual = (Date) typeMapper.mapToType("2010/12/11", Date.class, new String[] { "yyyy-MM-dd", "yyyy=MM=dd" });
        assertNull(actual);
    }

    @Test
    public void testMapToTypeToBigDecimal() throws Exception {
        BigDecimal actual = (BigDecimal) typeMapper.mapToType("1001.1", BigDecimal.class, null);
        assertThat(actual, is(new BigDecimal("1001.1")));
        actual = (BigDecimal) typeMapper.mapToType("1001.1", BigDecimal.class, new String[0]);
        assertThat(actual, is(new BigDecimal("1001.1")));
        actual = (BigDecimal) typeMapper.mapToType("57,311,001.11", BigDecimal.class, new String[] { "#,###,###" });
        assertThat(actual, is(new BigDecimal("57311001.11")));
        actual = (BigDecimal) typeMapper.mapToType("57,311,001.11", BigDecimal.class,
                new String[] { "invalid-format" });
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testMapToTypeArrayToString() throws Exception {
        String actual = (String) typeMapper.mapToType(new String[] { "a" }, String.class, null);
        assertThat(actual, is("a"));
        actual = (String) typeMapper.mapToType(new String[] { "a", "b" }, String.class, null);
        assertThat(actual, is("a"));
        actual = (String) typeMapper.mapToType(new String[0], String.class, null);
        assertThat(actual, is(nullValue()));
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
