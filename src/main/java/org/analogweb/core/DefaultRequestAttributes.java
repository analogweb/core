package org.analogweb.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class DefaultRequestAttributes implements RequestAttributes {

    private final Map<String, AttributesHandler> resolvers;
    private final InvocationMetadata metadata;

    public DefaultRequestAttributes(Collection<AttributesHandler> resolvers,InvocationMetadata metadata) {
        this.resolvers = Maps.newEmptyHashMap();
        for (AttributesHandler resolver : resolvers) {
            this.resolvers.put(resolver.getScopeName(), resolver);
        }
        this.metadata = metadata;
    }

    public DefaultRequestAttributes(Map<String, AttributesHandler> resolvers,InvocationMetadata metadata) {
        this.resolvers = resolvers;
        this.metadata = metadata;
    }

    @Override
    public Object getValueOfQuery(RequestContext requestContext, String resolverName,
            String attributeName) {
        AttributesHandler resolver = null;
        Map<String, AttributesHandler> resolverMap = getAttributesHandlersMap();
        if (StringUtils.isEmpty(resolverName)) {
            Object value;
            AttributesHandler defaultResolver;
            for (String defaultResolverName : getDefaultResolverNames()) {
                defaultResolver = resolverMap.get(defaultResolverName);
                if (defaultResolver != null) {
                    value = defaultResolver.resolveAttributeValue(requestContext,
                            getInvocationMetadata(), attributeName);
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else {
            resolver = resolverMap.get(resolverName);
        }
        if (resolver == null) {
            // TODO warn log.
            return null;
        }
        return resolver.resolveAttributeValue(requestContext, getInvocationMetadata(),
                attributeName);
    }
    
    @Override
    public Map<String,AttributesHandler> getAttributesHandlersMap(){
        return this.resolvers;
    }

    protected final InvocationMetadata getInvocationMetadata(){
        return this.metadata;
    }

    private static final List<String> defaultResolverNames = Arrays.asList("parameter","request","path");
    protected List<String> getDefaultResolverNames() {
        return defaultResolverNames;
    }

}
