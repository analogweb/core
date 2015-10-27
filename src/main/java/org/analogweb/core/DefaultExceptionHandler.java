package org.analogweb.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.Modules;
import org.analogweb.ModulesAware;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * Default {@link ExceptionHandler} implementation.
 * @author snowgoose
 */
public class DefaultExceptionHandler implements ExceptionHandler, ModulesAware {

	protected static final Log log = Logs.getLog(DefaultExceptionHandler.class);
	private List<ExceptionMapper> mappers;

	@Override
	public Object handleException(Exception exception) {
		Object result = verifyException(exception);
		if (result != null) {
			return result;
		}
		result = verifyCause(exception);
		if (result != null) {
			return result;
		}
		logThrowable(exception);
		return internalServerError(exception);
	}

	protected Object internalServerError(Exception e) {
		return HttpStatus.INTERNAL_SERVER_ERROR
				.byReasonOf(stackTraceToString(e));
	}

	private String stackTraceToString(Exception e) {
		StringWriter s = new StringWriter();
		PrintWriter p = new PrintWriter(s);
		e.printStackTrace(p);
		p.flush();
		return s.toString();
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

	protected List<ExceptionMapper> getExceptionMappers() {
		return Collections.unmodifiableList(this.mappers);
	}

	protected void logThrowable(Exception exception) {
		log.warn(exception.toString());
	}

	@Override
	public void dispose() {
		this.mappers.clear();
	}

	@Override
	public void setModules(Modules modules) {
		this.mappers = new ArrayList<ExceptionMapper>();
		this.mappers.addAll(modules.getExceptionMappers());
	}
}
