package org.analogweb.util;

import org.analogweb.exception.AssertionFailureException;

/**
 * @author snowgoose
 */
public final class Assertion {

    public static void notNull(Object anObject, String name) throws AssertionFailureException {
        if (anObject == null) {
            throw new AssertionFailureException(name);
        }
    }

}
