package org.analogweb.core;

import org.analogweb.Headers;
import org.analogweb.ResponseContext;
import org.analogweb.core.MapHeaders;
import org.analogweb.core.response.HttpStatus;

/**
 * @author snowgoose
 */
public abstract class AbstractResponseContext implements ResponseContext {

    private int status = HttpStatus.OK.getStatusCode();
    private final Headers headers;
    private boolean completed;

    public AbstractResponseContext() {
        this(new MapHeaders());
    }

    public AbstractResponseContext(Headers headers) {
        this.headers = headers;
    }

    @Override
    public Headers getResponseHeaders() {
        return headers;
    }

    protected int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }
    
    @Override
    public boolean completed(){
        return this.completed;
    }

    @Override
    public void ensure(){
        this.completed = true;
    }

}

