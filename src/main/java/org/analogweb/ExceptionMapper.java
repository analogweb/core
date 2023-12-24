package org.analogweb;

/**
 * @author snowgooseyk
 */
public interface ExceptionMapper extends MultiModule {

    /**
     * Return {@code true} when this {@link ExceptionMapper} can handle {@link Throwable}.
     *
     * @param throwable
     *            {@link Throwable}
     *
     * @return true - this {@link ExceptionMapper} can handle {@link Throwable}.
     */
    boolean isMatch(Throwable throwable);

    /**
     * Convert {@link Throwable} to handleable result.(like a {@link Renderable} )
     *
     * @param throwable
     *            {@link Throwable}
     *
     * @return handleable result.(like a {@link Renderable})
     */
    Object mapToResult(Throwable throwable);
}
