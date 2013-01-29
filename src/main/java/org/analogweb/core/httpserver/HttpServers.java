package org.analogweb.core.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

import org.analogweb.core.WebApplication;
import org.analogweb.exception.ApplicationRuntimeException;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * @author snowgoose
 *
 */
public class HttpServers {

    public static HttpServer create(URI uri) {
        return create(uri, new AnalogHandler(new WebApplication()));
    }

    public static HttpServer create(URI uri, HttpHandler handler) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
            server.createContext(uri.getPath(), handler);
            server.setExecutor(Executors.newCachedThreadPool());
            return server;
        } catch (IOException e) {
            // TODO replace
            throw new ApplicationRuntimeException(e) {
                private static final long serialVersionUID = 1L;
            };
        }
    }

}
