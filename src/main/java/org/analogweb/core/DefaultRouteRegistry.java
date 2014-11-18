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
    	Map<RequestPathMetadata, InvocationMetadata> readOnly = Collections.unmodifiableMap(actionMetadataMap);
    	for(InvocationMetadataFinder finder : finders){
    		InvocationMetadata found = finder.find(readOnly, requestContext);
    		if(found == null){
    			continue;
    		} else if (found instanceof InvocationMetadataFinder.Cacheable){
    			InvocationMetadataFinder.Cacheable cacheable = (InvocationMetadataFinder.Cacheable)found;
    			return update(requestContext.getRequestPath(),cacheable);
    		} else {
    			return found;
    		}
    	}
        return null;
    }
    
    protected InvocationMetadata update(RequestPathMetadata path, InvocationMetadataFinder.Cacheable metadata){
    	InvocationMetadata i = metadata.getCachable();
    	this.actionMetadataMap.put(path, i);
    	return i;
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
