package org.analogweb.core;

import org.analogweb.RequestPathMetadata;

/**
 * @author snowgoose
 */
public abstract class AbstractRequestPathMetadata implements RequestPathMetadata {

    @Override
    public boolean equals(Object other) {
        if (other instanceof RequestPathMetadata) {
            RequestPathMetadata rpm = (RequestPathMetadata) other;
            return getActualPath().equals(rpm.getActualPath())
                    && getRequestMethods().containsAll(rpm.getRequestMethods());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getActualPath().hashCode();
        for (String method : getRequestMethods()) {
            hash = 31 * hash + (method != null ? method.hashCode() : 0);
        }
        return hash;
    }
}
