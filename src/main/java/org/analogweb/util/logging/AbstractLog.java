package org.analogweb.util.logging;

import org.analogweb.util.MessageResource;
import org.analogweb.util.PropertyResourceBundleMessageResource;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Markers.SimpleMarker;

/**
 * @author snowgoose
 */
public abstract class AbstractLog implements Log {

    private String name;
    private MessageResource defaultMessageResource;

    public AbstractLog(String name, ClassLoader classLoader) {
        this.name = name;
        this.defaultMessageResource = createDefaultMessageResource(classLoader);
    }

    protected final String getName() {
        return this.name;
    }

    @Override
    public void log(String message) {
        log(message, new Object[0]);
    }

    @Override
    public void log(String message, Object... args) {
        log(SimpleMarker.valueOf(StringUtils.EMPTY), message, args);
    }

    @Override
    public void log(String message, Throwable t, Object... args) {
        log(SimpleMarker.valueOf(StringUtils.EMPTY), message, t, args);
    }

    @Override
    public void log(Marker marker, String message) {
        log(marker, message, new Object[0]);
    }

    @Override
    public void log(Marker marker, String message, Object... args) {
        log(getDefaultMessageResource(), marker, message, null, args);
    }

    @Override
    public void log(Marker marker, String message, Throwable t, Object... args) {
        log(getDefaultMessageResource(), marker, message, t, args);
    }

    @Override
    public void log(MessageResource messageResource, String message) {
        log(messageResource, message, new Object[0]);
    }

    @Override
    public void log(MessageResource messageResource, String message, Object... args) {
        log(messageResource, SimpleMarker.valueOf(StringUtils.EMPTY), message, args);
    }

    @Override
    public void log(MessageResource messageResource, String message, Throwable t, Object... args) {
        log(messageResource, SimpleMarker.valueOf(StringUtils.EMPTY), message, t, args);
    }

    @Override
    public void log(MessageResource messageResource, Marker marker, String message) {
        log(messageResource, marker, message, new Object[0]);
    }

    @Override
    public void log(MessageResource messageResource, Marker marker, String message, Throwable t, Object... args) {
        logInternal(messageResource, marker, message, t, args);
    }

    @Override
    public void log(MessageResource messageResource, Marker marker, String message, Object... args) {
        logInternal(messageResource, marker, message, null, args);
    }

    protected void logInternal(MessageResource messageResource, Marker marker, String message, Throwable t,
            Object[] args) {
        char prefix = message.charAt(0);
        switch (prefix) {
        case 'T':
            if (isTraceEnabled(marker)) {
                trace(marker, createMessage(messageResource, message, args), t);
            }
            break;
        case 'D':
            if (isDebugEnabled(marker)) {
                debug(marker, createMessage(messageResource, message, args), t);
            }
            break;
        case 'I':
            if (isInfoEnabled(marker)) {
                info(marker, createMessage(messageResource, message, args), t);
            }
            break;
        case 'W':
            if (isWarnEnabled(marker)) {
                warn(marker, createMessage(messageResource, message, args), t);
            }
            break;
        case 'E':
            if (isErrorEnabled(marker)) {
                error(marker, createMessage(messageResource, message, args), t);
            }
            break;
        default:
            break;
        }
    }

    protected String createMessage(MessageResource messageResource, String message, Object[] args) {
        return messageResource.getMessage(message, args);
    }

    protected MessageResource createDefaultMessageResource(ClassLoader classLoader) {
        return new PropertyResourceBundleMessageResource("analog-messages", classLoader);
    }

    protected MessageResource getDefaultMessageResource() {
        return this.defaultMessageResource;
    }

    @Override
    public void trace(String message) {
        trace(message, new Object[0]);
    }

    @Override
    public void debug(String message) {
        debug(message, new Object[0]);
    }

    @Override
    public void info(String message) {
        info(message, new Object[0]);
    }

    @Override
    public void warn(String message) {
        warn(message, new Object[0]);
    }

    @Override
    public void error(String message) {
        error(message, new Object[0]);
    }
}
