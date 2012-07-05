package org.analogweb.core;

import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMapping;
import org.analogweb.RequestPathMetadata;
import org.analogweb.util.Maps;


/**
 * @author snowgoose
 */
public class DefaultRequestPathMapping implements RequestPathMapping {

    private final Map<RequestPathMetadata, InvocationMetadata> actionMetadataMap = Maps
            .newConcurrentHashMap();

    @Override
    public InvocationMetadata findInvocationMetadata(RequestPath requestPath) {
        for (Entry<RequestPathMetadata, InvocationMetadata> pathEntry : actionMetadataMap
                .entrySet()) {
            if (pathEntry.getKey().match(requestPath)) {
                return pathEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public void mapInvocationMetadata(RequestPathMetadata requestPath,
            InvocationMetadata actionMethodMetadata) {
        this.actionMetadataMap.put(requestPath, actionMethodMetadata);
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
