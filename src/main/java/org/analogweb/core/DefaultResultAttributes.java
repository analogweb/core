package org.analogweb.core;

import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.exception.NotAvairableScopeException;
import org.analogweb.util.Assertion;
import org.analogweb.util.StringUtils;


/**
 * @author snowgoose
 */
public class DefaultResultAttributes implements ResultAttributes {

    private static final String DEFAULT_RESULT_ATTRIBUTES_PLACER_NAME = "request";
    private final Map<String, AttributesHandler> placers;

    public DefaultResultAttributes(Map<String, AttributesHandler> placers) {
        this.placers = placers;
    }

    @Override
    public void setValueOfQuery(RequestContext request, String placerName, String attributeName,
            Object value) {
        Assertion.notNull(request, RequestContext.class.getName());
        if (StringUtils.isEmpty(placerName)) {
            placerName = getDefaultResultAttributesPlacerName();
        }
        AttributesHandler placer = getResultAttributesPlacers().get(placerName);
        if (placer != null) {
            placer.putAttributeValue(request, attributeName, value);
        } else {
            throw new NotAvairableScopeException(placerName);
        }
    }

    private String getDefaultResultAttributesPlacerName() {
        return DEFAULT_RESULT_ATTRIBUTES_PLACER_NAME;
    }

    protected Map<String, AttributesHandler> getResultAttributesPlacers() {
        return this.placers;
    }

    @Override
    public void removeValueOfQuery(RequestContext request, String placerName, String attributeName) {
        Assertion.notNull(request, RequestContext.class.getName());
        if (StringUtils.isEmpty(placerName)) {
            placerName = getDefaultResultAttributesPlacerName();
        }
        AttributesHandler placer = getResultAttributesPlacers().get(placerName);
        if (placer != null) {
            placer.removeAttribute(request, attributeName);
        } else {
            throw new NotAvairableScopeException(placerName);
        }

    }

}
