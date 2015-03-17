package org.analogweb.core;

import org.analogweb.core.response.HttpStatus;

/**
 * @author snowgooseyk
 */
public class UnsupportedMediaTypeExceptionMapper extends
        TypeAssignableFromExceptionMapper<UnsupportedMediaTypeException> {

    @Override
    public Object mapToResult(Throwable throwable) {
        return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }
}
