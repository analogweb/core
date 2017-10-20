package org.analogweb.util.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.analogweb.util.ClassUtils;

/**
 * @author snowgoose
 */
public final class Logs {

	private static final String DEFAULT_LOG_IMPLEMENTATION = "org.analogweb.util.logging.LogImpl";

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getCanonicalName());
	}

	public static Log getLog(String name) {
		return getLog(name, Thread.currentThread().getContextClassLoader());
	}

	public static Log getLog(String name, ClassLoader loader) {
		return getLog(name, loader, DEFAULT_LOG_IMPLEMENTATION);
	}

	public static Log getLog(String name, ClassLoader loader,
			String logImplementationClassName) {
		Log logger = findImplementation(name, loader,
				logImplementationClassName);
		return logger;
	}

	@SuppressWarnings("unchecked")
	private static Log findImplementation(String name, ClassLoader loader,
			String logImplementationClassName) {
		try {
			Class<?> implementationClass = ClassUtils
					.forNameQuietly(logImplementationClassName);
			if (implementationClass == null) {
				return defaultImplementation(name, loader);
			}
			if (AbstractLog.class.isAssignableFrom(implementationClass)) {
				Constructor<AbstractLog> constructor = (Constructor<AbstractLog>) implementationClass
						.getConstructor(String.class, ClassLoader.class);
				return constructor.newInstance(name, loader);
			}
			Log l = defaultImplementation(name, loader);
			l.log(Markers.INIT_COMPONENT, "WU001005");
			l.log(Markers.INIT_COMPONENT, "WU001004", l);
			return l;
		} catch (InstantiationException e) {
			return defaultImplementationWithException(name, loader, e,
					"WU001001", logImplementationClassName);
		} catch (NoSuchMethodException e) {
			return defaultImplementationWithException(name, loader, e,
					"WU001002", logImplementationClassName);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			return defaultImplementationWithException(name, loader,
					(cause != null) ? cause : e, "WU001003");
		} catch (IllegalAccessException e) {
			return defaultImplementationWithException(name, loader, e,
					"WU001000");
		} catch (SecurityException e) {
			return defaultImplementationWithException(name, loader, e,
					"WU001000");
		}
	}

	private static Log defaultImplementationWithException(String name,
			ClassLoader loader, Throwable e, String message, Object... args) {
		Log l = defaultImplementation(name, loader);
		l.log(Markers.INIT_COMPONENT, message, e, args);
		l.log(Markers.INIT_COMPONENT, "WU001004", l);
		return l;
	}

	private static AbstractLog defaultImplementation(String name,
			ClassLoader classLoader) {
		return new JulLog(name, classLoader);
	}
}
