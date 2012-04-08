/**
 * 
 */
package org.analogweb.core;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;

/**
 * @author snowgoose
 *
 */
public class AbstractAttributesHandler implements AttributesHandler {

    @Override
    public String getScopeName() {
        return "";
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String query) {
        // nop
        return null;
    }

    @Override
    public void putAttributeValue(RequestContext requestContext, String query, Object value) {
        // nop
    }

    @Override
    public void removeAttribute(RequestContext requestContext, String query) {
        // nop
    }

}
