package org.analogweb.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.analogweb.util.ClassUtils;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgoose
 */
class JulLog extends AbstractLog {

	private static final String DEFAULT_LOG_CONFIG_IMPLEMENTATION = AbstractLog.class
			.getPackage().getName() + ".JulLogConfigImpl";
	private static JulLogConfig runtimeConfig;
	private final Logger log;

	JulLog(String name, ClassLoader classLoader) {
		super(name, classLoader);
		if (runtimeConfig == null) {
			runtimeConfig = configureViaRuntimeClass(classLoader);
		}
		log = runtimeConfig.createLogger(name);
	}

	private JulLogConfig configureViaRuntimeClass(ClassLoader classLoader) {
		Class<?> clazz = ClassUtils.forNameQuietly(
				DEFAULT_LOG_CONFIG_IMPLEMENTATION, classLoader);
		if (clazz != null) {
			Object obj = ReflectionUtils.getInstanceQuietly(clazz);
			if (obj instanceof JulLogConfig) {
				JulLogConfig config = (JulLogConfig) obj;
				config.configure(classLoader);
				return config;
			}
		}
		return JulLogConfig.SIMPLE;
	}

	@Override
	public void trace(String message, Throwable throwable) {
		log.log(Level.FINEST, message, throwable);
	}

	@Override
	public void trace(String message, Object... args) {
		log.log(Level.FINEST, message, args);
	}

	@Override
	public void trace(Marker marker, String message) {
		trace(message);
	}

	@Override
	public void trace(Marker marker, String message, Throwable throwable) {
		trace(message, throwable);
	}

	@Override
	public void trace(Marker marker, String message, Object... args) {
		trace(message, args);
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isLoggable(Level.FINEST);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isTraceEnabled();
	}

	@Override
	public void debug(String message, Throwable throwable) {
		log.log(Level.FINE, message, throwable);
	}

	@Override
	public void debug(String message, Object... args) {
		log.log(Level.FINE, message, args);
	}

	@Override
	public void debug(Marker marker, String message) {
		debug(message);
	}

	@Override
	public void debug(Marker marker, String message, Throwable throwable) {
		debug(message, throwable);
	}

	@Override
	public void debug(Marker marker, String message, Object... args) {
		debug(message, args);
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isLoggable(Level.FINE);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isDebugEnabled();
	}

	@Override
	public void info(String message, Throwable throwable) {
		log.log(Level.INFO, message, throwable);
	}

	@Override
	public void info(String message, Object... args) {
		log.log(Level.INFO, message, args);
	}

	@Override
	public void info(Marker marker, String message) {
		info(message);
	}

	@Override
	public void info(Marker marker, String message, Throwable throwable) {
		info(message, throwable);
	}

	@Override
	public void info(Marker marker, String message, Object... args) {
		info(message, args);
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isLoggable(Level.INFO);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isInfoEnabled();
	}

	@Override
	public void warn(String message, Object... args) {
		log.log(Level.WARNING, message, args);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		log.log(Level.WARNING, message, throwable);
	}

	@Override
	public void warn(Marker marker, String message) {
		warn(message);
	}

	@Override
	public void warn(Marker marker, String message, Throwable throwable) {
		warn(message, throwable);
	}

	@Override
	public void warn(Marker marker, String message, Object... args) {
		warn(message, args);
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isLoggable(Level.WARNING);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isWarnEnabled();
	}

	@Override
	public void error(String message, Throwable throwable) {
		log.log(Level.SEVERE, message, throwable);
	}

	@Override
	public void error(String message, Object... args) {
		log.log(Level.SEVERE, message, args);
	}

	@Override
	public void error(Marker marker, String message) {
		error(message);
	}

	@Override
	public void error(Marker marker, String message, Throwable throwable) {
		error(message, throwable);
	}

	@Override
	public void error(Marker marker, String message, Object... args) {
		error(message, args);
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isLoggable(Level.SEVERE);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isErrorEnabled();
	}

	@Override
	public String toString() {
		return "Logger Facade for java.util.logging.Logger";
	}
}
