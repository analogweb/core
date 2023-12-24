package org.analogweb.core;

import org.analogweb.RequestValueResolver;

/**
 * @author snowgooseyk
 */
public class InvalidRequestFormatException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -5549705070830618945L;
    private final Class<? extends RequestValueResolver> resolverType;

    public InvalidRequestFormatException(Class<? extends RequestValueResolver> resolverType) {
        super();
        this.resolverType = resolverType;
    }

    public InvalidRequestFormatException(Throwable t, Class<? extends RequestValueResolver> resolverType) {
        super(t);
        this.resolverType = resolverType;
    }

    public Class<? extends RequestValueResolver> getResolverType() {
        return this.resolverType;
    }
}
