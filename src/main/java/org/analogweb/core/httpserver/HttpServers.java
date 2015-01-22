package org.analogweb.core.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

import org.analogweb.Server;
import org.analogweb.core.WebApplication;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.util.StringUtils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * @author snowgoose
 */
public final class HttpServers {

    private HttpServers() {
        // nop.
    }

    public static Server create(String uri) {
        return create(URI.create(uri));
    }

    public static Server create(URI uri) {
        return create(uri, new AnalogHandler(new WebApplication()));
    }

    public static Server create(URI uri, HttpHandler handler) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
            String basePath = uri.getPath();
            if (StringUtils.isEmpty(basePath)) {
                basePath = "/";
            }
            server.createContext(basePath, handler);
            server.setExecutor(Executors.newCachedThreadPool());
            return new HttpServerDelegate(server, handler);
        } catch (IOException e) {
            // TODO replace
            throw new ApplicationRuntimeException(e) {

                private static final long serialVersionUID = 1L;
            };
        }
    }
}
