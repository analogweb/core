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
import org.analogweb.annotation.By;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.ArrayUtils;

/**
 * @author snowgoose
 */
public class ConsumesMediaTypeVerifier extends AbstractInvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            RequestValueResolvers resolvers) {
        Annotation[] ann = method.getAnnotations();
        RequestFormats formats = AnnotationUtils.findAnnotation(RequestFormats.class, ann);
        if (formats != null) {
            String[] expectMimes = formats.value();
            MediaType contentType = context.getContentType();
            if (ArrayUtils.isEmpty(expectMimes)) {
                if (mediaTypeUnsupported(contentType, method, resolvers)) {
                    return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                }
            } else {
                for (String expectMime : expectMimes) {
                    if (contentType.isCompatible(MediaTypes.valueOf(expectMime))) {
                        return super.prepareInvoke(method, args, metadata, context, converters,
                                resolvers);
                    }
                }
                return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            }
        }
        return super.prepareInvoke(method, args, metadata, context, converters, resolvers);
    }

    private boolean mediaTypeUnsupported(MediaType contentType, Method method,
            RequestValueResolvers handlers) {
        Annotation[][] parameterAnn = method.getParameterAnnotations();
        for (Annotation[] pa : parameterAnn) {
            By by = AnnotationUtils.findAnnotation(By.class, pa);
            if (by != null) {
                RequestValueResolver ha = handlers.findRequestValueResolver(by.value());
                if (ha instanceof SpecificMediaTypeAttirbutesHandler
                        && ((SpecificMediaTypeAttirbutesHandler) ha).supports(contentType)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getPrecedence() {
        return 1;
    }

}
