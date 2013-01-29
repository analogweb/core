package org.analogweb.core.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.exception.ApplicationRuntimeException;

import com.sun.net.httpserver.HttpExchange;

/**
 * @author snowgoose
 */
public class HttpExchangeResponseContext implements ResponseContext {

    private final HttpExchange exc;
    private int status = HttpURLConnection.HTTP_OK;
    private long length = -1;
    private final ResponseWriter writer;

    public HttpExchangeResponseContext(HttpExchange exc) {
        this.exc = exc;
        this.writer = new DefaultResponseWriter();
    }

    protected HttpExchange getHttpExchange() {
        return this.exc;
    }

    @Override
    public void commmit(RequestContext context) {
        HttpExchange ex = getHttpExchange();
        commitHeadersAndStatus(ex, context);
        try {
            ResponseEntity entity = getResponseWriter().getEntity();
            // no content.
            if (entity != null) {
                entity.writeInto(ex.getResponseBody());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void commitHeadersAndStatus(HttpExchange ex, RequestContext context) {
        com.sun.net.httpserver.Headers eh = ex.getResponseHeaders();
        Headers hs = getResponseHeaders();
        for (String name : hs.getNames()) {
            eh.put(name, hs.getValues(name));
        }
        int status = getStatus();
        try {
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                ex.sendResponseHeaders(status, -1);
            } else {
                long length = getContentLength();
                if (length == 0) {
                    ex.sendResponseHeaders(status, -1);
                } else if (length < 0) {
                    ex.sendResponseHeaders(status, 0);
                } else {
                    ex.sendResponseHeaders(status, length);
                }
            }
        } catch (IOException e) {
            // TODO
            throw new ApplicationRuntimeException(e) {
                private static final long serialVersionUID = 1L;
            };
        }
    }

    @Override
    public Headers getResponseHeaders() {
        return new HttpExchangeHeaders(getHttpExchange().getResponseHeaders());
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
