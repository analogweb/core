package org.analogweb.core;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.analogweb.exception.ApplicationRuntimeException;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class QueryParameters implements Parameters {

    private final RequestContext context;
    private Map<String, String[]> extracted;

    public QueryParameters(RequestContext context) {
        this.context = context;
    }

    protected Map<String, String[]> extract(RequestContext context) {

        Charset charset = Charset.defaultCharset();
        MediaType mp = context.getContentType();
        if (mp != null) {
            String c = mp.getParameters().get("charset");
            if (StringUtils.isNotEmpty(c)) {
                charset = Charset.forName(c);
            }
        }

        try {
            String encoded = resolveEncodedParameters(context, charset);
            return extractEncodedParams(encoded, charset);
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

    protected String resolveEncodedParameters(RequestContext context, Charset charset)
            throws IOException {
        return context.getRequestPath().getRequestURI().getQuery();
    }

    protected Map<String, String[]> extractEncodedParams(String encoded, Charset charset)
            throws IOException {
        Map<String, String[]> map = Maps.newEmptyHashMap();
        if (StringUtils.isEmpty(encoded)) {
            return map;
        }
        final List<String> tokenized = StringUtils.split(encoded, '&');
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
            this.extracted = extract(getRequestContext());
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

    protected RequestContext getRequestContext() {
        return this.context;
    }
}
