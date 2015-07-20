package org.analogweb;

/**
 * Metadata of {@link Invocation}.
 * @author snowgoose
 */
public interface InvocationMetadata {

    Class<?> getInvocationClass();

    String getMethodName();

    Class<?>[] getArgumentTypes();

    RequestPathMetadata getDefinedPath();
}
