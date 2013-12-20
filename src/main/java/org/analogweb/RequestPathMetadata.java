package org.analogweb;

import java.util.List;

/**
 * A entry point path.
 * @author snowgoose
 */
public interface RequestPathMetadata {

    /**
     * Obtain defined path.
     * @return path
     */
    String getActualPath();

    /**
     * Obtain defined (HTTP) request methods.
     * @return request methods
     */
    List<String> getRequestMethods();

    /**
     * Check own {@link RequestPath} match assigned one.
     * @param requestPath {@link RequestPath}
     * @return {@code true} - if matched {@link RequestPath}
     */
    boolean match(RequestPath requestPath);

}
