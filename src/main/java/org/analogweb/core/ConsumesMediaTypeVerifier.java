package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.RequestFormats;
import org.analogweb.annotation.Scope;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.ArrayUtils;

/**
 * @author snowgoose
 */
public class ConsumesMediaTypeVerifier extends AbstractInvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            AttributesHandlers handlers) {
        Annotation[] ann = method.getAnnotations();
        RequestFormats formats = AnnotationUtils.findAnnotation(RequestFormats.class, ann);
        if (formats != null) {
            String[] expectMimes = formats.value();
            MediaType contentType = context.getContentType();
            if (ArrayUtils.isEmpty(expectMimes)) {
                if (mediaTypeUnsupported(contentType, method, handlers)) {
                    return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                }
            } else {
                for (String expectMime : expectMimes) {
                    if (contentType.isCompatible(MediaTypes.valueOf(expectMime))) {
                        return super.prepareInvoke(method, args, metadata, context, converters,
                                handlers);
                    }
                }
                return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            }
        }
        return super.prepareInvoke(method, args, metadata, context, converters, handlers);
    }

    private boolean mediaTypeUnsupported(MediaType contentType, Method method,
            AttributesHandlers handlers) {
        Annotation[][] parameterAnn = method.getParameterAnnotations();
        for (Annotation[] pa : parameterAnn) {
            Scope sp = AnnotationUtils.findAnnotation(Scope.class, pa);
            if (sp != null) {
                AttributesHandler ha = handlers.get(sp.value());
                if (ha instanceof SpecificMediaTypeAttirbutesHandler
                        && ((SpecificMediaTypeAttirbutesHandler) ha).supports(contentType)) {
                    return false;
                }
            }
        }
        return true;
    }

}
