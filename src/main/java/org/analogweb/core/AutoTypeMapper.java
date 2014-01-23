package org.analogweb.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.analogweb.TypeMapper;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class AutoTypeMapper implements TypeMapper {

    private static final Log log = Logs.getLog(AutoTypeMapper.class);
    private final Map<ClassPair, TypeMapper> mappers = Maps.newEmptyHashMap();

    public AutoTypeMapper() {
        initAutoTypeMappers();
    }

    protected void initAutoTypeMappers() {
        putTypeMapper(String.class, Boolean.class, new StringToBoolean());
        putTypeMapper(String.class, boolean.class, new StringToBoolean());
        putTypeMapper(String.class, Character.class, new StringToCharactor());
        putTypeMapper(String.class, char.class, new StringToCharactor());
        putTypeMapper(String.class, Short.class, new StringToShort());
        putTypeMapper(String.class, short.class, new StringToShort());
        putTypeMapper(String.class, Double.class, new StringToDouble());
        putTypeMapper(String.class, double.class, new StringToDouble());
        putTypeMapper(String.class, Float.class, new StringToFloat());
        putTypeMapper(String.class, float.class, new StringToFloat());
        putTypeMapper(String.class, Integer.class, new StringToInteger());
        putTypeMapper(String.class, int.class, new StringToInteger());
        putTypeMapper(String.class, Long.class, new StringToLong());
        putTypeMapper(String.class, long.class, new StringToLong());
        putTypeMapper(String.class, Date.class, new StringToDate());
        putTypeMapper(String.class, BigDecimal.class, new StringToBigDecimal());
        putTypeMapper(String[].class, String.class, new StringArrayToString());
    }

    protected void putTypeMapper(Class<?> convertFrom, Class<?> convertTo, TypeMapper typeMapper) {
        mappers.put(ClassPair.valueOf(convertFrom, convertTo), typeMapper);
    }

    @Override
    public Object mapToType(Object from, Class<?> requiredType, String[] formats) {
        if (from == null) {
            return nullSafePrimitive(requiredType);
        }
        if (from.getClass().equals(requiredType)) {
            return from;
        }
        TypeMapper mapper = mappers.get(ClassPair.valueOf(from.getClass(), requiredType));
        if (mapper != null) {
            return mapper.mapToType(from, requiredType, formats);
        }
        return null;
    }

    private Object nullSafePrimitive(Class<?> requiredType) {
        if (requiredType.isPrimitive() == false) {
            return null;
        } else if (requiredType.equals(char.class)) {
            return Character.MIN_VALUE;
        } else if (requiredType.equals(short.class)) {
            return (short) 0;
        } else if (requiredType.equals(int.class)) {
            return 0;
        } else if (requiredType.equals(long.class)) {
            return 0L;
        } else if (requiredType.equals(double.class)) {
            return 0.0d;
        } else if (requiredType.equals(float.class)) {
            return 0.0f;
        } else if (requiredType.equals(byte.class)) {
            return (byte) 0;
        } else {
            // remains boolean only.
            return false;
        }
    }

    protected static final class ClassPair {

        private final Class<?> convertFrom;
        private final Class<?> convertTo;

        public static ClassPair valueOf(Class<?> convertFrom, Class<?> convertTo) {
            return new ClassPair(convertFrom, convertTo);
        }

        private ClassPair(Class<?> convertFrom, Class<?> convertTo) {
            Assertion.notNull(convertTo, "convert-to");
            Assertion.notNull(convertFrom, "convert-from");
            this.convertFrom = convertFrom;
            this.convertTo = convertTo;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ClassPair) {
                ClassPair otherPair = (ClassPair) other;
                return otherPair.convertFrom.equals(convertFrom)
                        && otherPair.convertTo.equals(convertTo);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + convertFrom.hashCode();
            hashCode = 31 * hashCode + convertTo.hashCode();
            return hashCode;
        }
    }

    private static final class StringToCharactor extends TypeSafeTypeMapper<String, Character> {

        @Override
        public Character mapToTypeInternal(String from, Class<Character> requiredType,
                String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                return from.toCharArray()[0];
            }
            return null;
        }
    }

    private static final class StringToBoolean extends TypeSafeTypeMapper<String, Boolean> {

        @Override
        public Boolean mapToTypeInternal(String from, Class<Boolean> requiredType, String[] formats) {
            return ((from.equalsIgnoreCase("true") || from.equalsIgnoreCase("yes") || from
                    .equalsIgnoreCase("on")));
        }
    }

    private static final class StringToShort extends TypeSafeTypeMapper<String, Short> {

        @Override
        public Short mapToTypeInternal(String from, Class<Short> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                try {
                    return Short.valueOf(from);
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToFloat extends TypeSafeTypeMapper<String, Float> {

        @Override
        public Float mapToTypeInternal(String from, Class<Float> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                try {
                    return Float.valueOf(from);
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToDouble extends TypeSafeTypeMapper<String, Double> {

        @Override
        public Double mapToTypeInternal(String from, Class<Double> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                try {
                    return Double.valueOf(from);
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToInteger extends TypeSafeTypeMapper<String, Integer> {

        @Override
        public Integer mapToTypeInternal(String from, Class<Integer> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                try {
                    return Integer.valueOf(from);
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToLong extends TypeSafeTypeMapper<String, Long> {

        @Override
        public Long mapToTypeInternal(String from, Class<Long> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from)) {
                try {
                    return Long.valueOf(from);
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToBigDecimal extends TypeSafeTypeMapper<String, BigDecimal> {

        @Override
        public BigDecimal mapToTypeInternal(String from, Class<BigDecimal> requiredType,
                String[] formats) {
            if (formats != null && formats.length > 0) {
                DecimalFormat formatter = new DecimalFormat();
                for (String formatPattern : formats) {
                    formatter.applyPattern(formatPattern);
                    formatter.setParseBigDecimal(true);
                    Number number = formatter.parse(from, new ParsePosition(0));
                    if (number != null) {
                        return (BigDecimal) number;
                    }
                }
            } else {
                return new BigDecimal(from);
            }
            return null;
        }
    }

    private static final class StringToDate extends TypeSafeTypeMapper<String, Date> {

        private static final String[] DEFAULT_FORMAT_PATTERNS = new String[] { "yyyy/MM/dd",
                "yyyy-MM-dd", "yyyy/MM/dd hh:mm:ss" };

        @Override
        public Date mapToTypeInternal(String from, Class<Date> requiredType, String[] formats) {
            SimpleDateFormat formatter = new SimpleDateFormat();
            if (formats == null || formats.length == 0) {
                formats = DEFAULT_FORMAT_PATTERNS;
            }
            for (String pattern : formats) {
                formatter.applyPattern(pattern);
                try {
                    return formatter.parse(from);
                } catch (ParseException e) {
                    // ignore.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringArrayToString extends TypeSafeTypeMapper<String[], String> {

        @Override
        public String mapToTypeInternal(String[] from, Class<String> requiredType, String[] formats) {
            if (ArrayUtils.isNotEmpty(from)) {
                return from[0];
            }
            return null;
        }
    }
}
