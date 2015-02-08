package org.analogweb.core;

import org.analogweb.RequestValueResolver;
import org.analogweb.util.StringUtils;

/**
 * {@link RequestValueResolver} throws this exception when specified type or name not resolvable.
 * @author snowgooseyk
 */
public class UnresolvableValueException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -5294948155134136581L;
    private final RequestValueResolver resolver;
    private final String typeName;
    private final String name;

    public UnresolvableValueException(RequestValueResolver resolver, Class<?> requiredType,
            String name) {
        this.typeName = (requiredType == null) ? StringUtils.EMPTY : requiredType
                .getCanonicalName();
        this.name = name;
        this.resolver = resolver;
    }

    public RequestValueResolver getRequestValueResolver() {
        return resolver;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getName() {
        return name;
    }
}
