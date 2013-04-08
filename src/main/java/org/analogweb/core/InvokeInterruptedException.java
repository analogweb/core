package org.analogweb.core;

/**
 * @author snowgoose
 */
public class InvokeInterruptedException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -257851055148255108L;
    private final Object interruption;

    public InvokeInterruptedException(Object interruption) {
        this.interruption = interruption;
    }

    public Object getInterruption() {
        return this.interruption;
    }
}
