package org.analogweb.core;

import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseEntity;

/**
 * @author y2k2mt
 */
public class DefaultResponse implements Response {

    private ResponseEntity entity;

    public DefaultResponse(ResponseEntity entity) {
        this.entity = entity;
    }

    @Override
    public ResponseEntity getEntity() {
        return entity;
    }

    public long getContentLength() {
        ResponseEntity e = getEntity();
        if (e != null) {
            return e.getContentLength();
        }
        return 0L;
    }

    @Override
    public void commit(RequestContext request, ResponseContext response) {
        try {
            response.commit(request, this);
        } finally {
            response.ensure();
        }
    }
}
