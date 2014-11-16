package org.analogweb.core;

import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.RouteRegistry;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class DefaultRouteRegistry implements RouteRegistry {

    private final Map<RequestPathMetadata, InvocationMetadata> actionMetadataMap = Maps
            .newConcurrentHashMap();

    @Override
    public InvocationMetadata findInvocationMetadata(RequestContext requestContext) {
    	RequestPath requestPath = requestContext.getRequestPath();
        // direct match
        InvocationMetadata direct = actionMetadataMap.get(requestPath);
        if (direct != null) {
            return direct;
        }
        // pattern match
        for (Entry<RequestPathMetadata, InvocationMetadata> pathEntry : actionMetadataMap
                .entrySet()) {
            if (pathEntry.getKey().match(requestPath)) {
                InvocationMetadata found = pathEntry.getValue();
                actionMetadataMap.put(requestPath, found);
                return found;
            }
        }
        return null;
    }

    @Override
    public void register(InvocationMetadata actionMethodMetadata) {
        this.actionMetadataMap.put(actionMethodMetadata.getDefinedPath(), actionMethodMetadata);
    }

    @Override
    public void dispose() {
        this.actionMetadataMap.clear();
    }

    @Override
    public String toString() {
        return this.actionMetadataMap.toString();
    }
}
