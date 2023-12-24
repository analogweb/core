package org.analogweb.core.response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.core.ResponseEvaluationException;
import org.analogweb.core.MissingRequirmentsException;
import org.analogweb.WebApplicationException;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public class Redirect implements Renderable {

    private static final Log log = Logs.getLog(Redirect.class);
    protected static final String DEFAULT_ENCODING_CHARSET = "UTF-8";
    protected static final int UNSET_RESPONSE_CODE = -1;
    private final String to;
    private final TreeMap<String, String> parameterMap = Maps.newTreeMap();
    private int responseCode = UNSET_RESPONSE_CODE;
    private String encoding = DEFAULT_ENCODING_CHARSET;

    private Redirect(String to) {
        this.to = to;
    }

    @Override
    public Response render(RequestContext context, ResponseContext response)
            throws IOException, WebApplicationException {
        Assertion.notNull(context, RequestContext.class.getCanonicalName());
        String path = getParametarizedPath();
        sendRedirect(context, response, URI.create(path), getResponseCode());
        return Response.EMPTY;
    }

    public Redirect encodeWith(String encodingCharset) {
        this.encoding = encodingCharset;
        return this;
    }

    protected void sendRedirect(RequestContext context, ResponseContext response, URI encodedPath, int responseCode)
            throws IOException {
        if (responseCode > 299 && responseCode < 400) {
            response.setStatus(responseCode);
        } else {
            // HTTP 1.0 compatible.
            if (responseCode != UNSET_RESPONSE_CODE) {
                log.log("WR000001", responseCode);
            }
            response.setStatus(HttpURLConnection.HTTP_MOVED_TEMP);
        }
        Headers responseHeader = response.getResponseHeaders();
        responseHeader.putValue("Location", encodedPath.toString());
    }

    protected String getTo() {
        return this.to;
    }

    protected Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.parameterMap);
    }

    protected String getParametarizedPath() {
        StringBuilder buffer = new StringBuilder(extractParameters(getTo()));
        if (getParameters().isEmpty() == false) {
            Set<Entry<String, String>> entries = getParameters().entrySet();
            Iterator<Entry<String, String>> entriesIterator = entries.iterator();
            Entry<String, String> entry = entriesIterator.next();
            try {
                buffer.append('?').append(URLEncoder.encode(entry.getKey(), getEncodingCharset())).append('=')
                        .append(URLEncoder.encode(entry.getValue(), getEncodingCharset()));
                while (entriesIterator.hasNext()) {
                    Entry<String, String> param = entriesIterator.next();
                    buffer.append('&').append(URLEncoder.encode(param.getKey(), getEncodingCharset())).append('=')
                            .append(URLEncoder.encode(param.getValue(), getEncodingCharset()));
                }
            } catch (UnsupportedEncodingException e) {
                throw new ResponseEvaluationException(e, this);
            }
        }
        return buffer.toString();
    }

    protected final String getEncodingCharset() {
        return this.encoding;
    }

    private String extractParameters(String originalPath) {
        int parameterIndex = 0;
        if ((parameterIndex = originalPath.indexOf("?")) > 0) {
            String query = originalPath.substring(parameterIndex + 1);
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                addParameter(keyValue[0], keyValue[1]);
            }
            return originalPath.substring(0, parameterIndex);
        } else {
            return originalPath;
        }
    }

    public static Redirect to(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new MissingRequirmentsException("redirect path", path);
        }
        return new Redirect(path);
    }

    public Redirect addParameter(String name, String value) {
        this.parameterMap.put(name, value);
        return this;
    }

    protected int getResponseCode() {
        return this.responseCode;
    }

    public Redirect resoposeCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Redirect) {
            return getParametarizedPath().equals(((Redirect) o).getParametarizedPath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int multiplier = 37;
        int result = 17;
        int hash = 0;
        String path = getParametarizedPath();
        if (path != null) {
            hash = path.hashCode();
        }
        result = multiplier * result + hash;
        return result;
    }
}
