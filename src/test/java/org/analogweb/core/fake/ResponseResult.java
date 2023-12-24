package org.analogweb.core.fake;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.analogweb.util.CollectionUtils;
import org.analogweb.util.Maps;

/**
 * Simulate {@link Application} responses.
 *
 * @author snowgooseyk
 */
public class ResponseResult {

    private int status = 200;
    private ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    private Map<String, List<String>> responseHeader = Maps.newEmptyHashMap();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public OutputStream getResponseBody() {
        return responseBody;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void add(String key, String value) {
        List<String> l = responseHeader.get(key);
        if (CollectionUtils.isEmpty(l)) {
            l = new ArrayList<String>();
        }
        l.add(value);
        responseHeader.put(key, l);
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String toBody() {
        return new String(responseBody.toByteArray());
    }
}
