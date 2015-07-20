package org.analogweb.core;

/**
 * @author snowgoose
 */
public class UnresolvableResultException extends ApplicationRuntimeException {

    private static final long serialVersionUID = 1L;
    private final Object invocationResult;

    public UnresolvableResultException(Object invocationResult) {
        super();
        this.invocationResult = invocationResult;
    }

    public Object getUnresolvableInvocationResult() {
        return this.invocationResult;
    }

    @Override
    public String getMessage() {
        return String.format("Unresolvable Result [%s].", getUnresolvableInvocationResult());
    }
}
