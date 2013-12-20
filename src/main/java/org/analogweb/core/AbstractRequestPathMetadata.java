package org.analogweb.core;

import org.analogweb.RequestPath;
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
        return getActualPath().hashCode();
    }

    @Override
    public boolean fulfill(RequestPath requestPath) throws UnsatisfiedRequestPathException {
    	return false;
    }

}
