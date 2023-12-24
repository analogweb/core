package org.analogweb.util;

import org.analogweb.RequestContext;
import org.analogweb.core.RequestContextWrapper;

/**
 * @author snowgooseyk
 */
public final class RequestContextResolverUtils {

    private RequestContextResolverUtils() {
        // nop.
    }

    @SuppressWarnings("unchecked")
    public static <T extends RequestContext> T resolveRequestContext(RequestContext incoming) {
        if (incoming instanceof RequestContextWrapper) {
            return (T) ((RequestContextWrapper) incoming).getOriginalRequestContext();
        }
        return (T) incoming;
    }
}
