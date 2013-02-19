package org.analogweb.core;

import org.analogweb.ExceptionHandler;
import org.analogweb.WebApplicationException;
import org.analogweb.core.direction.HttpStatus;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    protected static final Log log = Logs.getLog(DefaultExceptionHandler.class);

    public Object handleException(Exception exception) throws WebApplicationException {
        if (exception instanceof RequestMethodUnsupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED;
        }
        logThrowable(exception);
        throw new WebApplicationException(exception);
    }

    protected void logThrowable(Exception exception) {
        log.warn(exception.toString());
    }

}
