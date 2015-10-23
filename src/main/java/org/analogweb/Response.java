package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Holding response entity to write.
 * @author snowgooseyk
 */
public interface Response {

    void putEntity(InputStream entity);

    void putEntity(String entity);

    void putEntity(String entity, Charset charset);

    void putEntity(ResponseEntity entity);

    ResponseEntity getEntity();

    long getContentLength();

    void commit(RequestContext request, ResponseContext response);

    Response NOT_FOUND = new Response() {
    
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
            try{
                response.commit(request, this);
            } finally {
                response.ensure();
            }
        }
    };
    Response EMPTY = new Response() {
    
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
            try{
                response.commit(request, this);
            } finally {
                response.ensure();
            }
        }
    };
}
