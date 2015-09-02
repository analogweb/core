package org.analogweb;

import java.util.Collection;

/**
 * Factory of {@link InvocationMetadata} from class metadatas.
 * @author snowgoose
 */
public interface InvocationMetadataFactory extends MultiModule {

    boolean containsInvocationClass(Class<?> clazz);

    Collection<InvocationMetadata> createInvocationMetadatas(Class<?> clazz,ContainerAdaptor instanceProvider);
}
