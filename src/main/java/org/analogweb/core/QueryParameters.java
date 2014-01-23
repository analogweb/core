package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class QueryParameters implements Parameters {

    private Map<String, String[]> extracted;
    private final URI requestURI;
    private final InputStream body;
    private final MediaType contentType;

    public QueryParameters(URI requestURI) {
        this(requestURI, null, null);
    }

    public QueryParameters(URI requestURI, InputStream body, MediaType contentType) {
        this.requestURI = requestURI;
        this.body = body;
        this.contentType = contentType;
    }

    protected Map<String, String[]> extract(URI requestURI, InputStream body, MediaType contentType) {
        Charset charset = Charset.defaultCharset();
        if (contentType != null) {
            String c = contentType.getParameters().get("charset");
            if (StringUtils.isNotEmpty(c)) {
                charset = Charset.forName(c);
            }
        }
        try {
            String parameterParts = resolveParametersParts(requestURI, body, contentType, charset);
            return extractEncodedParams(parameterParts, charset, getParameterSeparator());
        } catch (IllegalArgumentException e) {
            throw new ApplicationRuntimeException(e) {

                // TODO
                private static final long serialVersionUID = 1L;
            };
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e) {

                // TODO
                private static final long serialVersionUID = 1L;
            };
        }
    }

    protected String resolveParametersParts(URI requestURI, InputStream body,
            MediaType contentType, Charset charset) throws IOException {
        return requestURI.getRawQuery();
    }

    protected char getParameterSeparator() {
        return '&';
    }

    protected Map<String, String[]> extractEncodedParams(String encoded, Charset charset,
            char separator) throws IOException {
        Map<String, String[]> map = Maps.newEmptyHashMap();
        if (StringUtils.isEmpty(encoded)) {
            return map;
        }
        final List<String> tokenized = StringUtils.split(encoded, separator);
        for (String token : tokenized) {
            int idx = token.indexOf('=');
            if (idx < 0) {
                map.put(URLDecoder.decode(token, charset.name()), (String[]) null);
            } else if (idx > 0) {
                String key = URLDecoder.decode(token.substring(0, idx), charset.name());
                String[] values;
                if (map.containsKey(key)) {
                    values = map.get(key);
                    values = ArrayUtils.add(String.class,
                            URLDecoder.decode(token.substring(idx + 1), charset.name()), values);
                } else {
                    values = ArrayUtils.newArray(URLDecoder.decode(token.substring(idx + 1),
                            charset.name()));
                }
                map.put(key, values);
            }
        }
        return map;
    }

    @Override
    public Map<String, String[]> asMap() {
        if (extracted == null) {
            this.extracted = extract(this.requestURI, this.body, this.contentType);
        }
        return extracted;
    }

    @Override
    public List<String> getValues(String key) {
        String[] array = asMap().get(key);
        if (ArrayUtils.isEmpty(array)) {
            return Collections.emptyList();
        }
        return Arrays.asList(array);
    }
}
