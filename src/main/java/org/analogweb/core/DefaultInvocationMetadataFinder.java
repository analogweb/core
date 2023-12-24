package org.analogweb.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;

/**
 * Default implementation of {@link org.analogweb.InvocationMetadataFinder}
 *
 * @author y2k2mt
 */
public class DefaultInvocationMetadataFinder extends AbstractInvocationMetadataFinder {

    @Override
    public InvocationMetadata find(Map<RequestPathMetadata, InvocationMetadata> metadatas, RequestContext request) {
        RequestPath requestPath = request.getRequestPath();
        // direct match
        InvocationMetadata direct = metadatas.get(requestPath);
        if (direct != null) {
            return direct;
        }
        // pattern match
        List<String> requireMethods = new ArrayList<String>();
        for (Entry<RequestPathMetadata, InvocationMetadata> pathEntry : metadatas.entrySet()) {
            try {
                if (pathEntry.getKey().match(requestPath)) {
                    return cacheable(pathEntry.getValue());
                }
            } catch (RequestMethodUnsupportedException e) {
                requireMethods.addAll(e.getDefinedMethods());
            }
        }
        if (!requireMethods.isEmpty()) {
            throw new RequestMethodUnsupportedException(requestPath, requireMethods, request.getRequestMethod());
        }
        return null;
    }
}
