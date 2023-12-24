package org.analogweb;

import java.net.URI;

/**
 * A path actually requested.
 *
 * @author y2k2mt
 */
public interface RequestPath extends RequestPathMetadata {

    /**
     * Requested HTTP method.
     *
     * @return Requested HTTP method.
     */
    String getRequestMethod();

    /**
     * Actually requested {@link URI}.
     *
     * @return {@link URI}
     */
    URI getRequestURI();

    /**
     * Application's context {@link URI}. mostly this {@link URI} contains protocol ,host and port.
     *
     * @return {@link URI}
     */
    URI getBaseURI();
}
