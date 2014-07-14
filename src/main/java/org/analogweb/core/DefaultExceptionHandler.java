package org.analogweb.core;

import org.analogweb.ExceptionHandler;
import org.analogweb.WebApplicationException;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public class DefaultExceptionHandler implements ExceptionHandler {

	protected static final Log log = Logs.getLog(DefaultExceptionHandler.class);

	public Object handleException(Exception exception)
			throws WebApplicationException {
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
		Throwable cause = null;
		if (exception instanceof InvocationFailureException
				&& (cause = exception.getCause()) != null) {
			Object causeResult = verifyException(cause);
			if (causeResult != null) {
				return causeResult;
			}
		}
		return null;
	}

	protected Object verifyException(Throwable exception) {
		if (exception == null) {
			return null;
		}
		if (exception instanceof UnsupportedMediaTypeException) {
			return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
		}
		if (exception instanceof RequestMethodUnsupportedException) {
			return HttpStatus.METHOD_NOT_ALLOWED;
		}
		return null;
	}

	protected void logThrowable(Exception exception) {
		log.warn(exception.toString());
	}
}
