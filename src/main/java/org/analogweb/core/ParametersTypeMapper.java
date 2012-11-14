package org.analogweb.core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.RequestContext;
import org.analogweb.annotation.Formats;
import org.analogweb.util.ReflectionUtils;

/**
 * リクエストパラメータを保持しているハッシュをオブジェクトのインスタンス
 * に変換する{@link AutoTypeMapper}の拡張です。<br/>
 * パラメータ名が一致するオブジェクトのフィールドに値を適用します。
 * 値の変換には継承元である{@link AutoTypeMapper}が使用されます。
 * また、オブジェクトはデフォルトのコンストラクタが定義されている必要が
 * あります。何らかの理由でオブジェクトを生成、またはパラメータの適用が
 * 不可能である場合は、nullを返します。
 * @author snowgoose
 */
public class ParametersTypeMapper extends AutoTypeMapper {

    @Override
    public Object mapToType(RequestContext context, Object from, Class<?> requiredType,
            String[] formats) {
        if (Map.class.isInstance(from)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) from;
            Object instance = ReflectionUtils.getInstanceQuietly(requiredType);
            if (instance != null) {
                mapToInstance(context, parameters, instance, formats);
                return instance;
            }
        }
        return null;
    }

    protected void mapToInstance(RequestContext context, Map<String, Object> parameters,
            Object instance, String[] formats) {
        for (Entry<String, Object> parameter : parameters.entrySet()) {
            Field field = ReflectionUtils.getAccessibleField(instance.getClass(),
                    parameter.getKey());
            if (field != null) {
                Class<?> fieldType = field.getType();
                Object value = extractParameter(parameter.getValue());
                if (!value.getClass().isAssignableFrom(fieldType)) {
                    Formats f = field.getAnnotation(Formats.class);
                    value = super.mapToType(context, value, fieldType, (f != null) ? f.value()
                            : formats);
                }
                ReflectionUtils.writeValueToField(field, instance, value);
            }
        }
    }

    private Object extractParameter(Object parameterValue) {
        if (String[].class.isInstance(parameterValue)) {
            String[] array = (String[]) parameterValue;
            if (array.length == 1) {
                return array[0];
            }
        }
        return parameterValue;
    }

}
