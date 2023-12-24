package org.analogweb.core;

import org.analogweb.MutableRequestContext;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;

/**
 * @author snowgooseyk
 */
public class DefaultMutableRequestContext extends RequestContextWrapper implements MutableRequestContext {

    private String overrideMethod;
    private RequestPath overridePath;

    public DefaultMutableRequestContext(RequestContext context) {
        super(context);
        this.overrideMethod = context.getRequestMethod();
        this.overridePath = context.getRequestPath();
    }

    @Override
    public void setRequestMethod(String method) {
        this.overrideMethod = method;
    }

    @Override
    public void setRequestPath(RequestPath path) {
        this.overridePath = path;
    }

    @Override
    public RequestContext unwrap() {
        return new RequestContextWrapper(getOriginalRequestContext()) {

            @Override
            public String getRequestMethod() {
                return overrideMethod;
            }

            @Override
            public RequestPath getRequestPath() {
                return overridePath;
            }
        };
    }
}
