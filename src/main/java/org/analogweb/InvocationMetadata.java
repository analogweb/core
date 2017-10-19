package org.analogweb;

import java.lang.reflect.Method;

/**
 * Metadata of {@link Invocation}.
 * 
 * @author snowgoose
 */
public interface InvocationMetadata {

	/**
	 * Get invocation {@link Class}.
	 * 
	 * @return {@link Class}
	 */
	Class<?> getInvocationClass();

	/**
	 * Get name of the invocation method.
	 * 
	 * @return name of the invocation method.
	 */
	String getMethodName();

	Class<?>[] getArgumentTypes();

	RequestPathMetadata getDefinedPath();

	/**
	 * Resolve {@link Method} by reflection.
	 * 
	 * @return {@link Method}
	 */
	Method resolveMethod();
}
