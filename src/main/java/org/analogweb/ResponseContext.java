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
     * @param response {@link Response}
     */
    void commmit(RequestContext context, Response response);

    Headers getResponseHeaders();

    void setStatus(int status);
    
    boolean completed();
    
    void ensure();

    public static interface Response {

        Response NOT_FOUND = new ResponseContext.Response() {

            @Override
            public void putEntity(InputStream entity) {
                //NOP
            }

            @Override
            public void putEntity(String entity) {
                //NOP
            }

            @Override
            public void putEntity(String entity, Charset charset) {
                //NOP
            }

            @Override
            public void putEntity(ResponseEntity entity) {
                //NOP
            }

            @Override
            public ResponseEntity getEntity() {
                //NOP
                return null;
            }

            @Override
            public long getContentLength() {
                //NOP
                return 0;
            }

            @Override
            public void commit(RequestContext request, ResponseContext response) {
                // NOP
            }

        };
        Response EMPTY = new ResponseContext.Response() {

            @Override
            public void putEntity(InputStream entity) {
                //NOP
            }

            @Override
            public void putEntity(String entity) {
                //NOP
            }

            @Override
            public void putEntity(String entity, Charset charset) {
                //NOP
            }

            @Override
            public void putEntity(ResponseEntity entity) {
                //NOP
            }

            ResponseEntity EMPTY_ENTITY = new ResponseEntity() {

                @Override
                public void writeInto(OutputStream responseBody) throws IOException {
                    //NOP
                }

                @Override
                public long getContentLength() {
                    //NOP
                    return 0;
                }
            };

            @Override
            public ResponseEntity getEntity() {
                //NOP
                return EMPTY_ENTITY;
            }

            @Override
            public long getContentLength() {
                return getEntity().getContentLength();
            }

            @Override
            public void commit(RequestContext request, ResponseContext response) {
                // NOP
            }

        };

        void putEntity(InputStream entity);

        void putEntity(String entity);

        void putEntity(String entity, Charset charset);

        void putEntity(ResponseEntity entity);

        ResponseEntity getEntity();
        
        void commit(RequestContext request,ResponseContext response);

        long getContentLength();
        
    }

    public static interface ResponseEntity {

        void writeInto(OutputStream responseBody) throws IOException;

        long getContentLength();
    }
}
