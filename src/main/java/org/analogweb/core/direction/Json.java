package org.analogweb.core.direction;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.analogweb.Direction;
import org.analogweb.DirectionFormatter;
import org.analogweb.RequestContext;
import org.analogweb.exception.FormatFailureException;
import org.analogweb.util.ArrayUtils;

/**
 * オブジェクトをJSON形式でフォーマットしてレスポンスする{@link Direction}です。<br/>
 * オブジェクト形式でフォーマットされた({}で囲まれた)JSONオブジェクトを生成します。
 * デフォルトのContent-Typeは「application/json; charset=UTF-8」です。
 * @author snowgoose
 */
public class Json extends TextFormattable<Json> {

    private static final String DEFAULT_JSON_CHARSET = "UTF-8";
    private static final String DEFAULT_JSON_CONTENT_TYPE = "application/json";

    public static Json as(Object obj) {
        Json json = new Json(obj);
        return json;
    }

    @SuppressWarnings("unchecked")
    public static Json with(String str) {
        Json json = new Json(str);
        return json;
    }

    protected Json(Object source) {
        super(source);
        super.as(DEFAULT_JSON_CONTENT_TYPE);
        super.withCharset(DEFAULT_JSON_CHARSET);
    }

    protected Json(String input) {
        super(input);
        super.as(DEFAULT_JSON_CONTENT_TYPE);
        super.withCharset(DEFAULT_JSON_CHARSET);
    }

    static class DefaultFormatter implements DirectionFormatter {

        @Override
        public void formatAndWriteInto(RequestContext writeTo, String charset, Object source)
                throws FormatFailureException {
            StringBuilder buffer = new StringBuilder();
            OutputStreamWriter output;
            try {
                format(buffer, source);
                output = new OutputStreamWriter(writeTo.getResponse().getOutputStream(), charset);
                output.write(buffer.toString());
                output.flush();
            } catch (IOException e) {
                throw new FormatFailureException(e, source, Json.class.getSimpleName());
            }
        }

        private void format(StringBuilder buffer, Object source) throws FormatFailureException,
                IOException {
            Class<?> clazz = source.getClass();
            if (clazz.isArray() || source instanceof Iterable) {
                buffer.append("{");
                formatNotNull(buffer, source, null);
            } else {
                BeanInfo info = null;
                try {
                    info = Introspector.getBeanInfo(clazz);
                } catch (IntrospectionException e) {
                    throw new FormatFailureException(e, source, Json.class.getSimpleName());
                }
                PropertyDescriptor[] properties = info.getPropertyDescriptors();
                if (ArrayUtils.isNotEmpty(properties)) {
                    buffer.append("{");
                }
                for (PropertyDescriptor desc : properties) {
                    if (desc.getDisplayName().equals("class")) {
                        continue;
                    }
                    Object propertyValue = readProperty(desc, source);
                    if (propertyValue == null) {
                        formatNull(buffer, desc);
                    } else {
                        formatNotNull(buffer, propertyValue, desc);
                    }
                }
            }
            buffer = buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("}");
        }

        private Object readProperty(PropertyDescriptor desc, Object instance)
                throws FormatFailureException {
            try {
                return desc.getReadMethod().invoke(instance);
            } catch (IllegalArgumentException e) {
                throw new FormatFailureException(e, instance, Json.class.getSimpleName());
            } catch (IllegalAccessException e) {
                throw new FormatFailureException(e, instance, Json.class.getSimpleName());
            } catch (InvocationTargetException e) {
                throw new FormatFailureException(e, instance, Json.class.getSimpleName());
            }
        }

        private void formatNull(StringBuilder buffer, PropertyDescriptor desc) throws IOException {
            buffer.append("\"");
            buffer.append(desc.getDisplayName());
            buffer.append("\"");
            buffer.append(": null,");
        }

        private void formatNotNull(StringBuilder buffer, Object value, PropertyDescriptor desc)
                throws IOException {
            if (desc != null) {
                buffer.append("\"");
                buffer.append(desc.getDisplayName());
                buffer.append("\"");
                buffer.append(": ");
            }
            if (value instanceof String) {
                buffer.append("\"");
                buffer.append(value.toString());
                buffer.append("\"");
            } else if (value instanceof Date) {
                buffer.append(String.valueOf(((Date) value).getTime()));
            } else if (value instanceof Number) {
                buffer.append(value.toString());
            } else if (value instanceof Boolean) {
                buffer.append(String.valueOf(Boolean.valueOf(value.toString())));
            } else if (value instanceof Iterable) {
                buffer.append("[");
                for (Object obj : (Iterable<?>) value) {
                    format(buffer, obj);
                    buffer.append(",");
                }
                buffer = buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("]");
            } else if (value.getClass().isArray()) {
                buffer.append("[");
                for (Object obj : (Object[]) value) {
                    format(buffer, obj);
                    buffer.append(",");
                }
                buffer = buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("]");
            }
            buffer.append(",");
        }
    }

    public static synchronized void flushCache() {
        Introspector.flushCaches();
    }

    /**
     * デフォルトの{@link ReplaceableFormatWriter}によってJSONのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatWriter}は全ての{@link Json}のインスタンスに適用されます。
     */
    @Override
    public DirectionFormatter getDefaultFormatter() {
        return new Json.DefaultFormatter();
    }

}
