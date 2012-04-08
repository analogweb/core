package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Formats;
import org.analogweb.annotation.MapWith;
import org.analogweb.annotation.Scope;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.StringUtils;

/**
 * アクションメソッドの引数から{@link As}が付与されている引数を検知し、 {@link As}
 * に定義されたスコープと属性名に応じた値をアクションメソッド の引数の値として設定する
 * {@link AbstractInvocationProcessor}の実装です。
 * @author snowgoose
 */
public class BindAttributeArgumentPreparator extends AbstractInvocationProcessor {

    @Override
    public Invocation prepareInvoke(Method method, Invocation invocation,
            InvocationMetadata metadata, RequestContext context, RequestAttributes attributes,
            TypeMapperContext converters) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] argTypes = metadata.getArgumentTypes();
        for (int index = 0, limit = argTypes.length; index < limit; index++) {
            Class<?> argType = argTypes[index];
            As bindAttribute = AnnotationUtils
                    .findAnnotation(As.class, parameterAnnotations[index]);
            if (bindAttribute != null) {
                Scope scope = AnnotationUtils.findAnnotation(Scope.class,
                        parameterAnnotations[index]);
                String scopeValue = StringUtils.EMPTY;
                if (scope != null) {
                    scopeValue = scope.value();
                }
                Object value = attributes.getValueOfQuery(context, scopeValue,
                        bindAttribute.value());
                if (value != null) {
                    MapWith mapWith = AnnotationUtils.findAnnotation(MapWith.class,
                            parameterAnnotations[index]);
                    Class<? extends TypeMapper> mapperType = TypeMapper.class;
                    if (mapWith != null) {
                        mapperType = mapWith.value();
                    }
                    Formats f = AnnotationUtils.findAnnotation(Formats.class,
                            parameterAnnotations[index]);
                    Object convertedValue = converters.mapToType(mapperType, context, attributes,
                            value, argType, (f != null) ? f.value() : new String[0]);
                    invocation.putPreparedArg(index, convertedValue);
                }
            }
        }
        return invocation;
    }

}
