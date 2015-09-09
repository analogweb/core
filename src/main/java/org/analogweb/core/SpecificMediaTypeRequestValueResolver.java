package org.analogweb.core;

import org.analogweb.MediaType;
import org.analogweb.RequestValueResolver;

/**
 * @author snowgoose
 */
public interface SpecificMediaTypeRequestValueResolver extends RequestValueResolver {

    /**
     * @param mediaType {@link MediaType}
     * @return Returns {@code true} when this resolver supports certain media type.
     */
    boolean supports(MediaType mediaType);
}
