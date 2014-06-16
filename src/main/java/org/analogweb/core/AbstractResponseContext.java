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
    private long length = -1;
    private final ResponseWriter writer;
    private final Headers headers;

    public AbstractResponseContext(ResponseWriter writer) {
        this(writer,new MapHeaders());
    }

    public AbstractResponseContext(ResponseWriter writer,Headers headers) {
        this.writer = writer;
        this.headers = headers;
    }

    @Override
    public Headers getResponseHeaders() {
        return headers;
    }

    @Override
    public ResponseWriter getResponseWriter() {
        return this.writer;
    }

    protected long getContentLength() {
        return this.length;
    }

    @Override
    public void setContentLength(long length) {
        this.length = length;
    }

    protected int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }
}
