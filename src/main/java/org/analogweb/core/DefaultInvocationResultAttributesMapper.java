package org.analogweb.core;

import java.util.Map.Entry;


import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.ResultAttributesHolder;
import org.analogweb.ScopedAttributeName;
import org.analogweb.util.StringUtils;


/**
 * @author snowgoose
 */
public class DefaultInvocationResultAttributesMapper extends AbstractInvocationProcessor {

    private static final String DEFAULT_SCOPE_NAME = "request";

    @Override
    public Object postInvoke(Object invocationResult, Invocation invocation,
            InvocationMetadata metadata, RequestContext context, RequestAttributes attributes,
            ResultAttributes resultAttributes) {
        if (invocationResult instanceof ResultAttributesHolder) {
            ResultAttributesHolder attributesHolder = ResultAttributesHolder.class
                    .cast(invocationResult);
            for (Entry<ScopedAttributeName, Object> attribute : attributesHolder.getAttributes()
                    .entrySet()) {
                ScopedAttributeName scopeAndAttributeName = attribute.getKey();
                String scopeName = scopeAndAttributeName.getScope();
                if (StringUtils.isEmpty(scopeName)) {
                    resultAttributes.setValueOfQuery(context, getDefaultScopeName(),
                            scopeAndAttributeName.getName(), attribute.getValue());
                } else {
                    resultAttributes.setValueOfQuery(context, scopeAndAttributeName.getScope(),
                            scopeAndAttributeName.getName(), attribute.getValue());
                }
            }
        }
        return invocationResult;
    }

    protected String getDefaultScopeName() {
        return DEFAULT_SCOPE_NAME;
    }

}
