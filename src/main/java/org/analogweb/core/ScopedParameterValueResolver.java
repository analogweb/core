package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.As;
import org.analogweb.annotation.By;
import org.analogweb.annotation.Formats;
import org.analogweb.annotation.MapWith;
import org.analogweb.util.AnnotationUtils;

/**
 * @author snowgoose
 */
public class ScopedParameterValueResolver implements AnnotatedInvocationParameterValueResolver {

    @Override
    public <T> T resolve(Annotation[] parameterAnnotations, Class<T> argType,
            RequestContext context, InvocationMetadata metadata, TypeMapperContext converters,
            RequestValueResolvers handlers) {
        As bindAttribute = AnnotationUtils.findAnnotation(As.class, parameterAnnotations);
        if (bindAttribute != null) {
            By scope = AnnotationUtils.findAnnotation(By.class, parameterAnnotations);
            RequestValueResolver handler;
            if(scope == null){
                handler = handlers.findDefaultRequestValueResolver();
            } else {
                handler = handlers.findRequestValueResolver(scope.value());
            }
            if (handler != null) {
                Object value = handler.resolveValue(context, metadata,
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
