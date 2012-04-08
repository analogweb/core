package org.analogweb.exception;

/**
 * @author snowgoose
 */
public class NotAvairableScopeException extends ApplicationRuntimeException {

    private static final long serialVersionUID = 2474750204709634945L;
    private final String attemptedScopeName;

    public NotAvairableScopeException(String attemptedScopeName) {
        this.attemptedScopeName = attemptedScopeName;
    }

    public String getAttemptedScopeName() {
        return this.attemptedScopeName;
    }

}
