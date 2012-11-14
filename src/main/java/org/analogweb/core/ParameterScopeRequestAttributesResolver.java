package org.analogweb.core;

import java.util.List;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class ParameterScopeRequestAttributesResolver extends AbstractAttributesHandler {

    private static final String NAME = "parameter";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String name, Class<?> requiredType) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        List<String> values = requestContext.getParameters().getValues(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (String[].class.equals(requiredType)) {
            return (values.isEmpty()) ? null : values.toArray(new String[values.size()]);
        }
        return values.get(0);
    }

}
