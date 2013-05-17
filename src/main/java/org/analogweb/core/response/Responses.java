package org.analogweb.core.response;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.core.DefaultResponseEntity;

public class Responses extends BuildableResponse<Responses> {

    private Responses() {
        // not instantiate.
    }

    public static Responses ok() {
        return new Responses().status(HttpStatus.OK);
    }

    public static Responses ok(ResponseEntity entity) {
        return ok().entity(entity);
    }

    public static Responses ok(InputStream body) {
        return ok(new DefaultResponseEntity(body));
    }

    public static Responses ok(String entity, Charset charset) {
        return ok(new DefaultResponseEntity(entity, charset));
    }

    public static Responses locates(URI location) {
        return new Responses().status(HttpStatus.FOUND).header("Location", location.toString());
    }
}
