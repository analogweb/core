package org.analogweb;

import java.util.Map;

/**
 * Media type.
 * @author snowgoose
 */
public interface MediaType {

    String getType();

    String getSubType();

    Map<String, String> getParameters();

    boolean isCompatible(MediaType other);
}
