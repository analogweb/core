package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author snowgoose
 */
public interface ResponseContext {

    /**
     * Commit response to stream.
     * @param context {@link RequestContext}
     */
    void commmit(RequestContext context);

    Headers getResponseHeaders();

    ResponseWriter getResponseWriter();

    void setStatus(int status);

    public static interface ResponseWriter {

        void writeEntity(InputStream entity);

        void writeEntity(String entity);

        void writeEntity(String entity, Charset charset);

        void writeEntity(ResponseEntity entity);

        ResponseEntity getEntity();
    }

    public static interface ResponseEntity {

        void writeInto(OutputStream responseBody) throws IOException;

        long getContentLength();
    }
}
