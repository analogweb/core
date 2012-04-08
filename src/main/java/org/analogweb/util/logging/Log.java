package org.analogweb.util.logging;

import org.analogweb.util.MessageResource;

/**
 * @author snowgoose
 */
public interface Log {

    void log(String message);

    void log(String message, Object... args);

    void log(String message, Throwable t, Object... args);

    void log(Marker marker, String message);

    void log(Marker marker, String message, Throwable t, Object... args);

    void log(Marker marker, String message, Object... args);

    void log(MessageResource messageResource, String message);

    void log(MessageResource messageResource, String message, Object... args);

    void log(MessageResource messageResource, String message, Throwable t, Object... args);

    void log(MessageResource messageResource, Marker marker, String message);

    void log(MessageResource messageResource, Marker marker, String message, Throwable t,
            Object... args);

    void log(MessageResource messageResource, Marker marker, String message, Object... args);

    void trace(String message);

    void trace(String message, Object... args);

    void trace(String message, Throwable throwable);

    void trace(Marker marker, String message);

    void trace(Marker marker, String message, Object... args);

    void trace(Marker marker, String message, Throwable throwable);

    boolean isTraceEnabled();

    boolean isTraceEnabled(Marker marker);

    void debug(String message);

    void debug(String message, Throwable throwable);

    void debug(String message, Object... args);

    void debug(Marker marker, String message);

    void debug(Marker marker, String message, Throwable throwable);

    void debug(Marker marker, String message, Object... args);

    boolean isDebugEnabled();

    boolean isDebugEnabled(Marker marker);

    void info(String message);

    void info(String message, Throwable throwable);

    void info(String message, Object... args);

    void info(Marker marker, String message);

    void info(Marker marker, String message, Throwable throwable);

    void info(Marker marker, String message, Object... args);

    boolean isInfoEnabled();

    boolean isInfoEnabled(Marker marker);

    void warn(String message);

    void warn(String message, Throwable throwable);

    void warn(String message, Object... args);

    void warn(Marker marker, String message);

    void warn(Marker marker, String message, Throwable throwable);

    void warn(Marker marker, String message, Object... args);

    boolean isWarnEnabled();

    boolean isWarnEnabled(Marker marker);

    void error(String message);

    void error(String message, Throwable throwable);

    void error(String message, Object... args);

    void error(Marker marker, String message);

    void error(Marker marker, String message, Throwable throwable);

    void error(Marker marker, String message, Object... args);

    boolean isErrorEnabled();

    boolean isErrorEnabled(Marker marker);

}
