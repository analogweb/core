package org.analogweb.core.httpserver;

import java.util.ArrayList;
import java.util.List;

import org.analogweb.Headers;

/**
 * @author snowgoose
 */
public class HttpExchangeHeaders implements Headers {

    private final com.sun.net.httpserver.Headers source;

    public HttpExchangeHeaders(com.sun.net.httpserver.Headers source) {
        this.source = source;
    }

    @Override
    public boolean contains(String name) {
        return this.source.containsKey(name);
    }

    @Override
    public List<String> getNames() {
        return new ArrayList<String>(this.source.keySet());
    }

    @Override
    public List<String> getValues(String name) {
        return this.source.get(name);
    }

    @Override
    public void putValue(String name, String value) {
        this.source.add(name, value);
    }

}
