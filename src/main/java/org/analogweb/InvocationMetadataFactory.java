package org.analogweb;

import java.util.Collection;

/**
 * Factory of {@link InvocationMetadata} from class metadatas.
 * @author y2k2mt
 */
public interface InvocationMetadataFactory extends MultiModule {

    Collection<InvocationMetadata> createInvocationMetadatas(ApplicationProperties properties,ContainerAdaptor instanceProvider);

}
