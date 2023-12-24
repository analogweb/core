package org.analogweb;

/**
 * Holding response entity to write.
 *
 * @author y2k2mt
 */
public interface Response {

    ResponseEntity getEntity();

    long getContentLength();

    void commit(RequestContext request, ResponseContext response);

    Response NOT_FOUND = new Response() {

        @Override
        public ResponseEntity getEntity() {
            // NOP
            return null;
        }

        @Override
        public long getContentLength() {
            // NOP
            return 0;
        }

        @Override
        public void commit(RequestContext request, ResponseContext response) {
            try {
                response.commit(request, this);
            } finally {
                response.ensure();
            }
        }
    };
    Response EMPTY = new Response() {

        ResponseEntity EMPTY_ENTITY = new ResponseEntity<String>() {

            @Override
            public String entity() {
                // NOP
                return "";
            }

            @Override
            public long getContentLength() {
                // NOP
                return 0;
            }
        };

        @Override
        public ResponseEntity getEntity() {
            // NOP
            return EMPTY_ENTITY;
        }

        @Override
        public long getContentLength() {
            return getEntity().getContentLength();
        }

        @Override
        public void commit(RequestContext request, ResponseContext response) {
            try {
                response.commit(request, this);
            } finally {
                response.ensure();
            }
        }
    };
}
