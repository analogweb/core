package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.util.Assertion;

/**
 * @author snowgooseyk
 */
public class RequestContextWrapper implements RequestContext {

    private RequestContext original;

    public RequestContextWrapper(RequestContext context) {
        Assertion.notNull(context, RequestContext.class.getName());
        this.original = context;
    }

    public RequestContext getOriginalRequestContext() {
        return this.original;
    }

    @Override
    public Cookies getCookies() {
        return getOriginalRequestContext().getCookies();
    }

    @Override
    public Headers getRequestHeaders() {
        return getOriginalRequestContext().getRequestHeaders();
    }

    @Override
    public Parameters getFormParameters() {
        return getOriginalRequestContext().getFormParameters();
    }

    @Override
    public Parameters getQueryParameters() {
        return getOriginalRequestContext().getQueryParameters();
    }

    @Override
    public Parameters getMatrixParameters() {
        return getOriginalRequestContext().getMatrixParameters();
    }

    @Override
    public InputStream getRequestBody() throws IOException {
        return getOriginalRequestContext().getRequestBody();
    }

    @Override
    public MediaType getContentType() {
        return getOriginalRequestContext().getContentType();
    }

    @Override
    public RequestPath getRequestPath() {
        return getOriginalRequestContext().getRequestPath();
    }

    @Override
    public Locale getLocale() {
        return getOriginalRequestContext().getLocale();
    }

    @Override
    public List<Locale> getLocales() {
        return getOriginalRequestContext().getLocales();
    }

    @Override
    public long getContentLength() {
        return getOriginalRequestContext().getContentLength();
    }

    @Override
    public String getCharacterEncoding() {
        return getOriginalRequestContext().getCharacterEncoding();
    }

    @Override
    public String getRequestMethod() {
        return getOriginalRequestContext().getRequestMethod();
    }

    @Override
    public <T> T getAttribute(String name) {
        return getOriginalRequestContext().getAttribute(name);
    }

    @Override
    public <T> void setAttribute(String name, T value) {
        getOriginalRequestContext().setAttribute(name, value);
    }
}
