package org.analogweb.exception;

/**
 * @author snowgoose
 */
public class MissingRequiredParameterException extends ApplicationConfigurationException {

    private static final long serialVersionUID = 6261526221128455478L;
    private final String parameterName;

    public MissingRequiredParameterException(String parameterName) {
        super((String) null);
        this.parameterName = parameterName;
    }

    public String getMissedParameterName() {
        return this.parameterName;
    }
}
