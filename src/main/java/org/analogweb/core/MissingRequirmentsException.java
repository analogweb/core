package org.analogweb.core;

/**
 * @author snowgoose
 */
public class MissingRequirmentsException extends UnresolvableResultException {

    private static final long serialVersionUID = -4724232642503365850L;
    private final String requirment;

    public MissingRequirmentsException(String requirment, Object invocationResult) {
        super(invocationResult);
        this.requirment = requirment;
    }

    public String getRequirment() {
        return this.requirment;
    }
}
