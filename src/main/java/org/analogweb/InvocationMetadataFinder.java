package org.analogweb;

import java.util.Map;

/**
 * Finder strategy of {@link InvocationMetadata} from {@link RouteRegistry}s
 * @author snowgooseyk
 */
public interface InvocationMetadataFinder extends Precedence, MultiModule {

    InvocationMetadata find(Map<RequestPathMetadata, InvocationMetadata> metadatas,
            RequestContext request);

    public interface Cacheable extends InvocationMetadata {

        InvocationMetadata getCachable();
    }
}
