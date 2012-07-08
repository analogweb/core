package org.analogweb.core;

import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.exception.UnsatisfiedRequestPathException;

/**
 * @author snowgoose
 */
public abstract class AbstractRequestPathMetadata implements RequestPathMetadata {

    @Override
    public boolean equals(Object other) {
        if (other instanceof RequestPathMetadata) {
            return getActualPath().equals(((RequestPathMetadata) other).getActualPath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getActualPath().hashCode();
    }

    @Override
    public void fulfill(RequestPath requestPath) throws UnsatisfiedRequestPathException {
        // do nothing.
    }

}
