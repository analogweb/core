package org.analogweb.core.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class HttpServerDelegate extends HttpServer {

    private HttpServer delegate;
    private HttpHandler handler;

    HttpServerDelegate(HttpServer delegate, HttpHandler handler) {
        this.delegate = delegate;
        this.handler = handler;
    }

    @Override
    public void bind(InetSocketAddress arg0, int arg1) throws IOException {
        delegate.bind(arg0, arg1);
    }

    @Override
    public HttpContext createContext(String arg0) {
        return delegate.createContext(arg0);
    }

    @Override
    public HttpContext createContext(String arg0, HttpHandler arg1) {
        return delegate.createContext(arg0, arg1);
    }

    @Override
    public InetSocketAddress getAddress() {
        return delegate.getAddress();
    }

    @Override
    public Executor getExecutor() {
        return delegate.getExecutor();
    }

    @Override
    public void removeContext(String arg0) throws IllegalArgumentException {
        delegate.removeContext(arg0);
    }

    @Override
    public void removeContext(HttpContext arg0) {
        delegate.removeContext(arg0);
    }

    @Override
    public void setExecutor(Executor arg0) {
        delegate.setExecutor(arg0);
    }

    @Override
    public void start() {
        delegate.start();
        if (handler instanceof AnalogHandler) {
            ((AnalogHandler) handler).run();
        }
    }

    @Override
    public void stop(int arg0) {
        if (handler instanceof AnalogHandler) {
            ((AnalogHandler) handler).shutdown();
        }
        delegate.stop(arg0);
    }

}
