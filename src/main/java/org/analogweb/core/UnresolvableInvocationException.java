package org.analogweb.core;

import org.analogweb.InvocationMetadata;

/**
 * @author snowgoose
 */
public class UnresolvableInvocationException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -7930360311054396569L;
    private InvocationMetadata sourceMetadata;

    public UnresolvableInvocationException(InvocationMetadata source) {
        this.sourceMetadata = source;
    }

    public InvocationMetadata getSourceMetadata() {
        return this.sourceMetadata;
    }
}
