package org.analogweb.core;

import org.analogweb.Direction;

/**
 * @author snowgoose
 */
public class DirectionEvaluationException extends ApplicationRuntimeException {

    private static final long serialVersionUID = 1L;
    private final Direction result;

    public DirectionEvaluationException(Throwable cause, Direction result) {
        super(cause);
        this.result = result;
    }

    public Direction getActionResult() {
        return this.result;
    }

}
