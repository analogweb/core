package org.analogweb.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
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
    public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
            Class<?> requiredType, String[] formats) {
        if (from == null) {
            return nullSafePrimitive(requiredType);
        }
        if (from.getClass().equals(requiredType)) {
            return from;
        }
        TypeMapper mapper = mappers.get(ClassPair.valueOf(from.getClass(), requiredType));
        if (mapper != null) {
            return mapper.mapToType(context, attributes, from, requiredType, formats);
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

    private static final class StringToCharactor implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                return from.toString().toCharArray()[0];
            }
            return null;
        }
    }

    private static final class StringToBoolean implements TypeMapper {

        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            String fromText = (String) from;
            return ((fromText.equalsIgnoreCase("true") || fromText.equalsIgnoreCase("yes") || fromText
                    .equalsIgnoreCase("on")));
        }

    }

    private static final class StringToShort implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                try {
                    return Short.valueOf(from.toString());
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToFloat implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                try {
                    return Float.valueOf(from.toString());
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToDouble implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                try {
                    return Double.valueOf(from.toString());
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToInteger implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                try {
                    return Integer.valueOf(from.toString());
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToLong implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (StringUtils.isNotEmpty(from.toString())) {
                try {
                    return Long.valueOf(from.toString());
                } catch (NumberFormatException e) {
                    // nop.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }
    }

    private static final class StringToBigDecimal implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (formats != null && formats.length > 0) {
                DecimalFormat formatter = new DecimalFormat();
                for (String formatPattern : formats) {
                    formatter.applyPattern(formatPattern);
                    formatter.setParseBigDecimal(true);
                    Object number = formatter.parse(from.toString(), new ParsePosition(0));
                    if (number != null) {
                        return number;
                    }
                }
            } else {
                return new BigDecimal(from.toString());
            }
            return null;
        }
    }

    private static final class StringToDate implements TypeMapper {

        private static final String[] DEFAULT_FORMAT_PATTERNS = new String[] { "yyyy/MM/dd",
                "yyyy-MM-dd", "yyyy/MM/dd hh:mm:ss" };

        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            SimpleDateFormat formatter = new SimpleDateFormat();
            if (formats == null || formats.length == 0) {
                formats = DEFAULT_FORMAT_PATTERNS;
            }
            String fromText = (String) from;
            for (String pattern : formats) {
                formatter.applyPattern(pattern);
                try {
                    return formatter.parse(fromText);
                } catch (ParseException e) {
                    // ignore.
                    log.log(Markers.VARIABLE_ACCESS, "TV000004", e, from, requiredType);
                }
            }
            return null;
        }

    }

    private static final class StringArrayToString implements TypeMapper {
        @Override
        public Object mapToType(RequestContext context, RequestAttributes attributes, Object from,
                Class<?> requiredType, String[] formats) {
            if (String[].class.isInstance(from)) {
                String[] array = (String[]) from;
                if (ArrayUtils.isNotEmpty(array)) {
                    return array[0];
                }
            }
            return null;
        }
    }

}
