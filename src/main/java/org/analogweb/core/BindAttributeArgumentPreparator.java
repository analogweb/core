package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
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
 * エントリポイントメソッドの引数から{@link As}が付与されている引数を検知し、 {@link As}
 * に定義されたスコープと属性名に応じた値をエントリポイントメソッド の引数の値として設定する
 * {@link AbstractInvocationProcessor}の実装です。
 * @author snowgoose
 */
public class BindAttributeArgumentPreparator extends AbstractInvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            AttributesHandlers handlers) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] argTypes = metadata.getArgumentTypes();
        for (int index = 0, limit = argTypes.length; index < limit; index++) {
            Object convertedValue = convert(context, metadata, converters, argTypes[index],
                    parameterAnnotations[index], handlers);
            if (convertedValue != null) {
                args.putInvocationArgument(index, convertedValue);
            }
        }
        return NO_INTERRUPTION;
    }

    protected Object convert(RequestContext context, InvocationMetadata metadata,
            TypeMapperContext converters, Class<?> argType, Annotation[] parameterAnnotations,
            AttributesHandlers handlers) {
        As bindAttribute = AnnotationUtils.findAnnotation(As.class, parameterAnnotations);
        if (bindAttribute != null) {
            Scope scope = AnnotationUtils.findAnnotation(Scope.class, parameterAnnotations);
            String scopeValue = StringUtils.EMPTY;
            if (scope != null) {
                scopeValue = scope.value();
            }
            AttributesHandler handler = handlers.get(scopeValue);
            if (handler != null) {
                Object value = handler.resolveAttributeValue(context, metadata,
                        bindAttribute.value(), argType);
                if (value != null) {
                    MapWith mapWith = AnnotationUtils.findAnnotation(MapWith.class,
                            parameterAnnotations);
                    Class<? extends TypeMapper> mapperType = TypeMapper.class;
                    if (mapWith != null) {
                        mapperType = mapWith.value();
                    }
                    Formats f = AnnotationUtils.findAnnotation(Formats.class, parameterAnnotations);
                    Object convertedValue = converters.mapToType(mapperType, context, value,
                            argType, (f != null) ? f.value() : new String[0]);
                    return convertedValue;
                }
            }
        }
        return null;
    }

}
