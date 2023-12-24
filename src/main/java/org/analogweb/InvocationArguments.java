package org.analogweb;

/**
 * Parameters of {@link Invocation#invoke()}.
 *
 * @author snowgoose
 */
public interface InvocationArguments extends PreparedInvocationArguments {

    /**
     * @param newInvocationInstance
     *            instance of entry-point.
     */
    void replace(Object newInvocationInstance);

    /**
     * @param index
     *            index of argument.
     * @param arg
     *            value of argument.
     */
    void putInvocationArgument(int index, Object arg);
}
