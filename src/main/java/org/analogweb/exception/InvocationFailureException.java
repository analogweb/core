package org.analogweb.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.analogweb.InvocationMetadata;


/**
 * @author snowgoose
 */
public class InvocationFailureException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -7102039116582504501L;
    private final InvocationMetadata metadata;
    private final List<Object> args;

    public InvocationFailureException(Throwable cause, InvocationMetadata metadata, Object[] args) {
        super(cause);
        this.metadata = metadata;
        this.args = Arrays.asList(args);
    }

    public InvocationMetadata getMetadata() {
        return metadata;
    }

    public Object[] getArgs() {
        return args.toArray(new Object[args.size()]);
    }

    @Override
    public Throwable getCause() {
        Throwable th = super.getCause();
        if (th instanceof InvocationTargetException) {
            InvocationTargetException iv = (InvocationTargetException) th;
            return iv.getCause();
        }
        return th;
    }

}
