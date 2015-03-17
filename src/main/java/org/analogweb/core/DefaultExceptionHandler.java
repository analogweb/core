package org.analogweb.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.WebApplicationException;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * Default {@link ExceptionHandler} implementation.
 * @author snowgoose
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    protected static final Log log = Logs.getLog(DefaultExceptionHandler.class);
    private List<ExceptionMapper> mappers = getDefaultMappers();

    @Override
    public Object handleException(Exception exception) throws WebApplicationException {
        Object result = verifyException(exception);
        if (result != null) {
            return result;
        }
        result = verifyCause(exception);
        if (result != null) {
            return result;
        }
        logThrowable(exception);
        throw new WebApplicationException(exception);
    }

    protected Object verifyCause(Exception exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            Object result = handleMappers(cause);
            if (result != null) {
                return result;
            }
            cause = cause.getCause();
        }
        return null;
    }

    protected Object verifyException(Throwable exception) {
        if (exception == null) {
            return null;
        }
        return handleMappers(exception);
    }

    private Object handleMappers(Throwable t) {
        for (ExceptionMapper mapper : getExceptionMappers()) {
            Object result;
            if (mapper.isMatch(t) && (result = mapper.mapToResult(t)) != null) {
                return result;
            }
        }
        return null;
    }

    private List<ExceptionMapper> getDefaultMappers() {
        return Arrays.<ExceptionMapper> asList(new UnsupportedMediaTypeExceptionMapper(),
                new InvalidRequestFormatExceptionMapper(),
                new RequestMethodUnsupportedExceptionMapper());
    }

    protected List<ExceptionMapper> getExceptionMappers() {
        return Collections.unmodifiableList(this.mappers);
    }

    protected void logThrowable(Exception exception) {
        log.warn(exception.toString());
    }
}
