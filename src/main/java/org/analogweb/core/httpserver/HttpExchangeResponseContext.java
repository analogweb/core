package org.analogweb.core.httpserver;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.analogweb.RequestContext;
import org.analogweb.core.AbstractResponseContext;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.MapHeaders;

import com.sun.net.httpserver.HttpExchange;

/**
 * @author snowgoose
 */
public class HttpExchangeResponseContext extends AbstractResponseContext {

    protected static long NO_CONTENT = -1;
    protected static long CHUNKED = 0;
    private final HttpExchange exc;

	public HttpExchangeResponseContext(HttpExchange exc) {
		super(new DefaultResponseWriter(), new MapHeaders(
				exc.getResponseHeaders()));
		this.exc = exc;
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
            ex.getResponseBody().flush();
            ex.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void commitHeadersAndStatus(HttpExchange ex, RequestContext context) {
        int status = getStatus();
        try {
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                ex.sendResponseHeaders(status, NO_CONTENT);
            } else {
                long length = getContentLength();
                if (length == 0) {
                    ex.sendResponseHeaders(status, NO_CONTENT);
                } else if (length < 0) {
                    ex.sendResponseHeaders(status, CHUNKED);
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

}
