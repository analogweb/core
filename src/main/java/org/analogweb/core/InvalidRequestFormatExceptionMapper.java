package org.analogweb.core;

import org.analogweb.core.response.HttpStatus;

/**
 * @author snowgooseyk
 */
public class InvalidRequestFormatExceptionMapper extends
        TypeAssignableFromExceptionMapper<InvalidRequestFormatException> {

    @Override
    public Object mapToResult(Throwable throwable) {
        return HttpStatus.BAD_REQUEST;
    }
}
