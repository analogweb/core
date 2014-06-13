package org.analogweb.core.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.analogweb.Headers;
import org.analogweb.RequestPath;
import org.analogweb.core.AbstractRequestContext;
import org.analogweb.core.MapHeaders;

import com.sun.net.httpserver.HttpExchange;

/**
 * @author snowgoose
 */
public class HttpExchangeRequestContext extends AbstractRequestContext {

    private final HttpExchange ex;

    HttpExchangeRequestContext(HttpExchange ex, RequestPath requestPath, Locale defaultLocale) {
    	super(requestPath,defaultLocale);
        this.ex = ex;
    }

    protected HttpExchange getHttpExchange() {
        return this.ex;
    }

    @Override
    public InputStream getRequestBody() throws IOException {
        return getHttpExchange().getRequestBody();
    }

    @Override
    public Headers getRequestHeaders() {
        return new MapHeaders(getHttpExchange().getRequestHeaders());
    }
}
