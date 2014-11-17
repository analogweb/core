package org.analogweb.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.RequestContext;
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
    public InvocationMetadata findInvocationMetadata(RequestContext requestContext,List<InvocationMetadataFinder> finders) {
    	Map<RequestPathMetadata, InvocationMetadata> metadatas = Collections.unmodifiableMap(actionMetadataMap);
    	for(InvocationMetadataFinder finder : finders){
    		InvocationMetadata found = finder.find(metadatas, requestContext);
    		if(found == null){
    			continue;
    		} else if (found instanceof InvocationMetadataFinder.Cacheable){
    			return ((InvocationMetadataFinder.Cacheable)found).getCachable();
    		} else {
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
