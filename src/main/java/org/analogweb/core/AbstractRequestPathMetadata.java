package org.analogweb.core;

import org.analogweb.RequestPathMetadata;

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

}
