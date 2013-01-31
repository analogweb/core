package org.analogweb.core.httpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;

import org.junit.After;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

public class HttpServersTest {

    private HttpServer server;

    @After
    public void tearDown() {
        // Release port and dispose WebApplication.
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testCreate() {
        assumeThat(portAvairable(28080), is(false));
        server = HttpServers.create(URI.create("http://localhost:28080/"));
        assertThat(server, is(not(nullValue())));
    }

    @Test
    public void testCreateWithAbsorutePath() {
        assumeThat(portAvairable(28080), is(false));
        server = HttpServers.create(URI.create("http://localhost:28080"));
        assertThat(server, is(not(nullValue())));
    }

    private boolean portAvairable(int port) {
        ServerSocket sock = null;
        try {
            sock = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
