package org.analogweb.core;

import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.ResultAttributes;
import org.analogweb.ResultAttributesFactory;


/**
 * @author snowgoose
 */
public class DefaultResultAttributesFactory implements ResultAttributesFactory {

    @Override
    public ResultAttributes createResultAttributes(Map<String, AttributesHandler> placers) {
        return new DefaultResultAttributes(placers);
    }

}
