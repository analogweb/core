package org.analogweb.core;

import org.analogweb.core.response.HttpStatus;

/**
 * @author snowgooseyk
 */
public class RequestMethodUnsupportedExceptionMapper
        extends TypeAssignableFromExceptionMapper<RequestMethodUnsupportedException> {

    @Override
    public Object mapToResult(Throwable throwable) {
        return HttpStatus.METHOD_NOT_ALLOWED;
    }
}
