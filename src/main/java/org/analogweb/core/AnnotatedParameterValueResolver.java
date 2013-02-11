package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public interface AnnotatedParameterValueResolver {

    <T> T resolve(Annotation[] parameterAnnotations, Class<T> argType, RequestContext context,
            InvocationMetadata metadata, TypeMapperContext converters, AttributesHandlers handlers);

}
