package org.analogweb;

/**
 * Handle {@link Throwable} that caused by invoking route method.
 * @author snowgoose
 */
public interface ExceptionHandler extends Module {

    /**
     * Handle caused {@link Throwable}.
     * The return value handle by {@link ResponseResolver} when that is not {@code null}. 
     * @param exception {@link Exception}
     * @throws WebApplicationException
     * @return Result of exception handling.({@link Renderable} etc...)
     */
    Object handleException(Exception exception) throws WebApplicationException;
}
