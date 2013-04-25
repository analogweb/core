package org.analogweb.core;

import org.analogweb.Renderable;

/**
 * @author snowgoose
 */
public class ResponseEvaluationException extends ApplicationRuntimeException {

    private static final long serialVersionUID = 1L;
    private final Renderable result;

    public ResponseEvaluationException(Throwable cause, Renderable result) {
        super(cause);
        this.result = result;
    }

    public Renderable getActionResult() {
        return this.result;
    }

}
