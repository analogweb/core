package org.analogweb;

/**
 * Invocation of entry-point method.
 * @author snowgoose
 */
public interface Invocation {

    /**
     * Invoke entry-point method.
     * @see ApplicationProcessor
     * @return result of method invocation.
     */
    Object invoke();

    /**
     * Get entry-point object instance.
     * @return entry-point object instance.
     */
    Object getInvocationInstance();

    /**
     * Get {@link InvocationArguments}
     * @return {@link InvocationArguments}
     */
    InvocationArguments getInvocationArguments();
}
