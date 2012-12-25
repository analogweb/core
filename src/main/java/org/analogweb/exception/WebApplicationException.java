package org.analogweb.exception;

/**
 * @author snowgoose
 */
public class WebApplicationException extends Exception implements ApplicationException {

    private static final long serialVersionUID = 7186804233007757288L;

    public WebApplicationException(Throwable cause) {
        super(cause);
    }

}
