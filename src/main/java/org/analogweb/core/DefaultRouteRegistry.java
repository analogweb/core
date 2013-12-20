package org.analogweb.core;

import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.InvocationMetadata;
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
	public InvocationMetadata findInvocationMetadata(RequestPath requestPath) {
		// direct match
		InvocationMetadata found = actionMetadataMap.get(requestPath);
		if (found != null) {
			found.getDefinedPath().fulfill(requestPath);
			return found;
		}
		// pattern match
		for (Entry<RequestPathMetadata, InvocationMetadata> pathEntry : actionMetadataMap
				.entrySet()) {
			if (pathEntry.getKey().match(requestPath)) {
				found = pathEntry.getValue();
				found.getDefinedPath().fulfill(requestPath);
				return found;
			}
		}
		return null;
	}

	@Override
	public void register(InvocationMetadata actionMethodMetadata) {
		this.actionMetadataMap.put(actionMethodMetadata.getDefinedPath(),
				actionMethodMetadata);
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
