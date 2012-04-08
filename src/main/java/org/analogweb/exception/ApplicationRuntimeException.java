package org.analogweb.exception;

/**
 * @author snowgoose
 */
public abstract class ApplicationRuntimeException extends RuntimeException implements
        ApplicationException {

    private static final long serialVersionUID = -5102446288115183932L;

    public ApplicationRuntimeException() {
        super();
    }

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(Throwable cause) {
        super(cause);
    }

}
