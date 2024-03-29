package org.analogweb.core;

import java.lang.reflect.Method;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestPathMetadata;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgoose
 */
public class DefaultInvocationMetadata implements InvocationMetadata {

    private final Class<?> actionClass;
    private final String methodName;
    private final Class<?>[] argumentType;
    private final RequestPathMetadata definedPath;

    public DefaultInvocationMetadata(Class<?> actionClass, String methodName, Class<?>[] argumentType,
            RequestPathMetadata definedPath) {
        super();
        this.actionClass = actionClass;
        this.methodName = methodName;
        this.argumentType = ArrayUtils.clone(Class.class, argumentType);
        this.definedPath = definedPath;
    }

    @Override
    public Class<?> getInvocationClass() {
        return actionClass;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getArgumentTypes() {
        return ArrayUtils.clone(Class.class, argumentType);
    }

    @Override
    public RequestPathMetadata getDefinedPath() {
        return this.definedPath;
    }

    @Override
    public Method resolveMethod() {
        return ReflectionUtils.getInvocationMethod(this);
    }

    @Override
    public String toString() {
        return String.format(super.toString() + " on %s", getDefinedPath().toString());
    }

}
