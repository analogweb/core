package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.util.IOUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * Resolve request body as {@link InputStream} or {@link String}.
 * returns {@code null} when request body not readable.
 * @see RequestContext#getRequestBody()
 * @author snowgoose
 */
public class RequestBodyValueResolver implements RequestValueResolver {

    private Log log = Logs.getLog(RequestBodyValueResolver.class);

    @Override
    public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
            String query, Class<?> type, Annotation[] annotations) {
        if (type == null) {
            return null;
        }
        try {
            if (InputStream.class.isAssignableFrom(type)) {
                return requestContext.getRequestBody();
            } else if (String.class.isAssignableFrom(type)) {
                return IOUtils.toString(requestContext.getRequestBody());
            }
            log.log(Markers.BOOT_APPLICATION, "WV000001",
                    RequestBodyValueResolver.class.getCanonicalName(), type.getCanonicalName());
            throw new UnresolvableValueException(this, type, query);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e) {
                private static final long serialVersionUID = 1L;
            };
        }
    }
}
