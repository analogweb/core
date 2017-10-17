package org.analogweb;

import org.analogweb.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Holding response entity to write.
 * @author y2k2mt
 */
public interface Response {

    void putEntity(ResponseEntity entity);

    ResponseEntity getEntity();

    long getContentLength();

    void commit(RequestContext request, ResponseContext response);

    Response NOT_FOUND = new Response() {

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
        public void putEntity(ResponseEntity entity) {
            //NOP
        }
    
        ResponseEntity EMPTY_ENTITY = new ResponseEntity<String>() {

            @Override
            public String entity() {
                //NOP
                return "";
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
