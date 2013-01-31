package org.analogweb.core;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;

/**
 * {@link AttributesHandler}の既定の実装です。<br/>
 * {@link AttributesHandler}の必要最低限のメソッドの拡張を支援します。
 * @author snowgoose
 */
public class AbstractAttributesHandler implements AttributesHandler {

    @Override
    public String getScopeName() {
        return "";
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String key, Class<?> requiredType) {
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
