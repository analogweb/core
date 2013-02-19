package org.analogweb.core;

/**
 * @author snowgoose
 */
public class MissingModuleException extends ApplicationConfigurationException {

    private static final long serialVersionUID = 998032904612234430L;
    private final Class<?> requiredModuleClass;

    public MissingModuleException(Class<?> requiredModuleClass) {
        super(requiredModuleClass.toString());
        this.requiredModuleClass = requiredModuleClass;
    }

    public Class<?> getRequiredModuleClass() {
        return this.requiredModuleClass;
    }

}
