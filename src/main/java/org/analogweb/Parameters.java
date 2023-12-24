package org.analogweb;

import java.util.List;
import java.util.Map;

/**
 * Request parameters.
 *
 * @author snowgoose
 */
public interface Parameters {

    List<String> getValues(String key);

    Map<String, String[]> asMap();
}
