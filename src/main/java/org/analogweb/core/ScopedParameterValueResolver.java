package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
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
 * @author snowgoose
 */
public class ScopedParameterValueResolver implements AnnotatedParameterValueResolver {

    @Override
    public <T> T resolve(Annotation[] parameterAnnotations, Class<T> argType,
            RequestContext context, InvocationMetadata metadata, TypeMapperContext converters,
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
                    T convertedValue = converters.mapToType(mapperType, value, argType,
                            (f != null) ? f.value() : new String[0]);
                    return convertedValue;
                }
            }
        }
        return null;
    }

}
