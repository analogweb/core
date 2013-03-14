package org.analogweb.core;

import org.analogweb.Response;

/**
 * @author snowgoose
 */
public class ResponseEvaluationException extends ApplicationRuntimeException {

    private static final long serialVersionUID = 1L;
    private final Response result;

    public ResponseEvaluationException(Throwable cause, Response result) {
        super(cause);
        this.result = result;
    }

    public Response getActionResult() {
        return this.result;
    }

}
