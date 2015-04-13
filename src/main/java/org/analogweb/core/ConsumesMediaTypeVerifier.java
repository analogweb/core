package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.annotation.Resolver;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.ArrayUtils;

/**
 * @author snowgoose
 */
public class ConsumesMediaTypeVerifier extends AbstractApplicationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            RequestValueResolvers resolvers) {
        if (method == null || context.getRequestMethod().equalsIgnoreCase("GET")) {
            return NO_INTERRUPTION;
        }
        Annotation[] ann = method.getAnnotations();
        RequestFormats formats = AnnotationUtils.findAnnotation(RequestFormats.class, ann);
        MediaType contentType = context.getContentType();
        if (contentType == null) {
            throw new UnsupportedMediaTypeException(metadata.getDefinedPath());
        }
        String[] expectMimes;
        if (formats == null || ArrayUtils.isEmpty(expectMimes = formats.value())) {
            if (mediaTypeUnsupported(contentType, method, resolvers)) {
                throw new UnsupportedMediaTypeException(metadata.getDefinedPath());
            }
        } else {
            for (String expectMime : expectMimes) {
                if (contentType.isCompatible(MediaTypes.valueOf(expectMime))) {
                    return super.prepareInvoke(method, args, metadata, context, converters,
                            resolvers);
                }
            }
            throw new UnsupportedMediaTypeException(metadata.getDefinedPath());
        }
        return super.prepareInvoke(method, args, metadata, context, converters, resolvers);
    }

    private boolean mediaTypeUnsupported(MediaType contentType, Method method,
            RequestValueResolvers handlers) {
        Annotation[][] parameterAnn = method.getParameterAnnotations();
        for (Annotation[] pa : parameterAnn) {
            Resolver by = AnnotationUtils.findAnnotation(Resolver.class, pa);
            if (by != null) {
                RequestValueResolver ha = handlers.findRequestValueResolver(by.value());
                if (ha instanceof SpecificMediaTypeRequestValueResolver
                        && ((SpecificMediaTypeRequestValueResolver) ha).supports(contentType) == false) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
