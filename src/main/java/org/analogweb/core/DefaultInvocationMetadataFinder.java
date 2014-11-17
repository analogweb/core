package org.analogweb.core;

import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;

/**
 * @author snowgooseyk
 *
 */
public class DefaultInvocationMetadataFinder extends
		AbstractInvocationMetadataFinder {

	@Override
	public InvocationMetadata find(Map<RequestPathMetadata, InvocationMetadata> metadatas,RequestContext request) {
    	RequestPath requestPath = request.getRequestPath();
        // direct match
        InvocationMetadata direct = metadatas.get(requestPath);
        if (direct != null) {
            return direct;
        }
        // pattern match
        for (Entry<RequestPathMetadata, InvocationMetadata> pathEntry : metadatas
                .entrySet()) {
            if (pathEntry.getKey().match(requestPath)) {
                return cacheable(pathEntry.getValue());
            }
        }
        return null;
	}

}
