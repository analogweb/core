package org.analogweb.core;

import org.analogweb.RequestPathMetadata;

/**
 * Throws when requested Content-Type not verified.
 * @author snowgooseyk
 */
public class UnsupportedMediaTypeException extends UnsatisfiedRequestException {

    private static final long serialVersionUID = 851954945303138560L;

    public UnsupportedMediaTypeException(RequestPathMetadata metadata) {
        super(metadata);
    }
}
