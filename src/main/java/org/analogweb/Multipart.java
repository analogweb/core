package org.analogweb;

import java.io.InputStream;

/**
 * Multiple part request.
 *
 * @author snowgoose
 */
public interface Multipart {

    String getName();

    String getResourceName();

    InputStream getInputStream();

    byte[] getBytes();

    String getContentType();
}
