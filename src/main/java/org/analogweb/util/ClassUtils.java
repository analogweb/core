package org.analogweb.util;

/**
 * @author snowgoose
 */
public final class ClassUtils {

	public static Class<?> forNameQuietly(String canonicalName) {
		return forNameQuietly(canonicalName, Thread.currentThread()
				.getContextClassLoader());
	}

	public static Class<?> forNameQuietly(String canonicalName,
			ClassLoader classLoader) {
		try {
			return Class.forName(canonicalName, false, classLoader);
		} catch (ClassNotFoundException ignore) {
			return null;
		}
	}
}
