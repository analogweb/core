package org.analogweb.core.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

/**
 * @author snowgoose
 */
public class MockHttpExchange extends HttpExchange {

    private InputStream requestBody;
    private OutputStream responseBody = new ByteArrayOutputStream();
    private int sendStatus;
    private long contentLength;
    private String requestMethod;
    private URI requestURI;
    private InetSocketAddress localAddress;
    private HttpContext httpContext;
    private boolean exchangeClosed;
    private Headers requestHeaders;
    private Headers responseHeaders;

    public int getSendStatus() {
        return this.sendStatus;
    }

    public long getResponseContentLength() {
        return this.contentLength;
    }

    public boolean isExchangeClosed() {
        return exchangeClosed;
    }

    @Override
    public void close() {
        this.exchangeClosed = true;
    }

    @Override
    public Object getAttribute(String arg0) {
        return null;
    }

    @Override
    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public HttpPrincipal getPrincipal() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public InputStream getRequestBody() {
        return this.requestBody;
    }

    @Override
    public Headers getRequestHeaders() {
        return this.requestHeaders;
    }

    @Override
    public String getRequestMethod() {
        return this.requestMethod;
    }

    @Override
    public URI getRequestURI() {
        return this.requestURI;
    }

    @Override
    public OutputStream getResponseBody() {
        return this.responseBody;
    }

    @Override
    public int getResponseCode() {
        return 0;
    }

    @Override
    public Headers getResponseHeaders() {
        return this.responseHeaders;
    }

    @Override
    public void sendResponseHeaders(int arg0, long arg1) throws IOException {
        this.sendStatus = arg0;
        this.contentLength = arg1;
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
    	// nop.
    }

    @Override
    public void setStreams(InputStream arg0, OutputStream arg1) {
        this.requestBody = arg0;
        this.responseBody = arg1;
    }

    public void setResponseBody(ByteArrayOutputStream responseBody) {
        this.responseBody = responseBody;
    }

    public void setRequestBody(InputStream requestBody) {
        this.requestBody = requestBody;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void setResponseHeaders(Headers headers) {
        this.responseHeaders = headers;
    }

    public void setRequestHeaders(Headers headers) {
        this.requestHeaders = headers;
    }
}
