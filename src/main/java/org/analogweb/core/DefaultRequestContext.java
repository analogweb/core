package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.Parameters;
import org.analogweb.RequestPath;
import org.analogweb.ServletRequestContext;
import org.analogweb.exception.ApplicationRuntimeException;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class DefaultRequestContext implements ServletRequestContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private Cookies cookies;
    private ServletRequestHeaders requestHeaders;
    private ServletResponseHeaders responseHeaders;
    private ServletParameters parameters;

    public DefaultRequestContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return response;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public RequestPath getRequestPath() {
        HttpServletRequest request = getServletRequest();
        URI uri = createRequestURI(request);
        return new DefaultRequestPath(getServletContext().getContextPath(), uri,
                request.getMethod());
    }

    protected URI createRequestURI(HttpServletRequest request) {
        try {
            return new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(),
                    request.getRequestURI(), request.getQueryString(), null);
        } catch (URISyntaxException e) {
            // TODO better exception.
            throw new ApplicationRuntimeException(e){
                private static final long serialVersionUID = 1L;
            };
        }
    }

    @Override
    public Cookies getCookies() {
        if (this.cookies == null) {
            this.cookies = new ServletCookies(getServletRequest(), getServletResponse());
        }
        return this.cookies;
    }

    static class ServletCookies implements Cookies {
        private Map<String, Cookies.Cookie> cookieMap;
        private HttpServletResponse response;

        ServletCookies(HttpServletRequest request, HttpServletResponse response) {
            this.cookieMap = Maps.newEmptyHashMap();
            for (javax.servlet.http.Cookie c : request.getCookies()) {
                this.cookieMap.put(c.getName(), new ServletCookie(c));
            }
            this.response = response;
        }

        @Override
        public Cookie getCookie(String name) {
            return this.cookieMap.get(name);
        }

        @Override
        public void putCookie(String name, Object value) {
            this.response.addCookie(new javax.servlet.http.Cookie(name, value.toString()));
        }

        @Override
        public void putCookie(Cookie cookie) {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(),
                    cookie.getValue());
            c.setComment(cookie.getComment());
            c.setDomain(cookie.getDomain());
            c.setMaxAge(cookie.getMaxAge());
            c.setPath(cookie.getPath());
            c.setSecure(cookie.isSecure());
            c.setVersion(cookie.getVersion());
            this.response.addCookie(c);
        }
    }

    static class ServletCookie implements Cookies.Cookie {

        private javax.servlet.http.Cookie cookie;

        ServletCookie(javax.servlet.http.Cookie cookie) {
            this.cookie = cookie;
        }

        @Override
        public String getName() {
            return this.cookie.getName();
        }

        @Override
        public String getValue() {
            return this.cookie.getValue();
        }

        @Override
        public String getComment() {
            return this.cookie.getComment();
        }

        @Override
        public String getPath() {
            return this.cookie.getPath();
        }

        @Override
        public int getMaxAge() {
            return this.cookie.getMaxAge();
        }

        @Override
        public boolean isSecure() {
            return this.cookie.getSecure();
        }

        @Override
        public int getVersion() {
            return this.cookie.getVersion();
        }

        @Override
        public String getDomain() {
            return this.cookie.getDomain();
        }

    }

    @Override
    public Headers getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ServletRequestHeaders(getServletRequest());
        }
        return this.requestHeaders;
    }

    static class ServletRequestHeaders implements Headers {

        private HttpServletRequest request;

        ServletRequestHeaders(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<String> getValues(String name) {
            return Collections.list(this.request.getHeaders(name));
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<String> getNames() {
            return Collections.list(this.request.getHeaderNames());
        }

        @Override
        public void putValue(String name, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(String name) {
            return this.request.getHeader(name) != null;
        }

    }

    @Override
    public Parameters getParameters() {
        if (this.parameters == null) {
            this.parameters = new ServletParameters(getServletRequest());
        }
        return this.parameters;
    }

    class ServletParameters implements Parameters {

        private HttpServletRequest request;

        ServletParameters(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public List<String> getValues(String key) {
            String[] values = request.getParameterValues(key);
            if (ArrayUtils.isEmpty(values)) {
                return Arrays.asList(request.getParameter(key));
            }
            return Arrays.asList(values);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map<String, String[]> asMap() {
            return request.getParameterMap();
        }

    }

    @Override
    public InputStream getRequestBody() throws IOException {
        return getServletRequest().getInputStream();
    }

    @Override
    public OutputStream getResponseBody() throws IOException {
        return getServletResponse().getOutputStream();
    }

    @Override
    public Headers getResponseHeaders() {
        if (this.responseHeaders == null) {
            this.responseHeaders = new ServletResponseHeaders(getServletRequest(),
                    getServletResponse());
        }
        return this.responseHeaders;
    }

    @Override
    public void setResponseStatus(int status) {
        getServletResponse().setStatus(status);
    }

    static class ServletResponseHeaders extends ServletRequestHeaders {

        private HttpServletResponse response;

        ServletResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
            super(request);
            this.response = response;
        }

        @Override
        public void putValue(String name, String value) {
            this.response.addHeader(name, value);
        }

    }
}
