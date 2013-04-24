package org.analogweb.core;

import java.util.List;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class ParameterValueResolver implements RequestValueResolver {

    @Override
    public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
            String name, Class<?> requiredType) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        List<String> values = requestContext.getQueryParameters().getValues(name);
        if (values == null || values.isEmpty()) {
            values = requestContext.getFormParameters().getValues(name);
            if (values == null || values.isEmpty()) {
                return null;
            }
        }
        if (String[].class.equals(requiredType)) {
            return (values.isEmpty()) ? null : values.toArray(new String[values.size()]);
        }
        return values.get(0);
    }
}