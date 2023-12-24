package org.analogweb.core.response;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.core.MediaTypes;
import org.analogweb.WebApplicationException;
import org.analogweb.util.StringUtils;

/**
 * @author y2k2mt
 */
public class Acceptable implements Renderable {

    private Object source;
    protected static final Map<String, Creator> DEFAULT_MEDIA_TYPE_MAP = new HashMap<String, Creator>() {

        private static final long serialVersionUID = 1L;

        {
            put(MediaTypes.APPLICATION_JSON, Creators.json());
            put(MediaTypes.WILDCARD, Creators.json());
        }
    };
    private Map<String, Creator> mediaTypeMap;

    public static Acceptable as(Object obj) {
        return new Acceptable(obj);
    }

    protected Acceptable(Object obj) {
        this.source = obj;
        this.mediaTypeMap = new HashMap<String, Creator>(DEFAULT_MEDIA_TYPE_MAP);
    }

    @Override
    public Response render(RequestContext context, ResponseContext response)
            throws IOException, WebApplicationException {
        List<String> mediaTypes = getAcceptableMediaType(context);
        if (mediaTypes.isEmpty()) {
            return HttpStatus.NOT_ACCEPTABLE.render(context, response);
        }
        Renderable d = selectResponse(mediaTypes, getSource());
        if (d == null) {
            return HttpStatus.NOT_ACCEPTABLE.render(context, response);
        }
        return d.render(context, response);
    }

    public Acceptable mapToAny(Renderable matchesAny) {
        return map(matchesAny, MediaTypes.WILDCARD);
    }

    public Acceptable map(Renderable matches, String... mediaTypesStartWith) {
        for (String mediaTypeStartWith : mediaTypesStartWith) {
            putToMediaTypeMap(mediaTypeStartWith, Creators.self(matches));
        }
        return this;
    }

    public Renderable selectAcceptableOne(RequestContext context) {
        Renderable r = selectResponse(getAcceptableMediaType(context), getSource());
        if (r == null) {
            return HttpStatus.NOT_ACCEPTABLE;
        }
        return r;
    }

    private Renderable selectResponse(List<String> mediaTypes, Object source) {
        for (String mediaType : mediaTypes) {
            for (Entry<String, Creator> entry : getMediaTypeMap().entrySet()) {
                if (StringUtils.trimToEmpty(mediaType).startsWith(entry.getKey())) {
                    return entry.getValue().create(source);
                }
            }
        }
        return null;
    }

    private List<String> getAcceptableMediaType(RequestContext context) {
        Headers headers = context.getRequestHeaders();
        return headers.getValues("Accept");
    }

    protected Object getSource() {
        return this.source;
    }

    protected Map<String, Creator> getMediaTypeMap() {
        return this.mediaTypeMap;
    }

    protected void putToMediaTypeMap(String mediaType, Creator c) {
        this.mediaTypeMap.put(mediaType, c);
    }

    static final class AcceptHeaderComparator implements Comparator<String> {

        @Override
        public int compare(String arg0, String arg1) {
            if (arg0.contains("*")) {
                if (arg1.contains("*")) {
                    if (StringUtils.trimToEmpty(arg0).startsWith("*/")) {
                        return 1;
                    } else if (StringUtils.trimToEmpty(arg1).startsWith("*/")) {
                        return -1;
                    }
                    List<String> s0 = StringUtils.split(arg0, ';');
                    List<String> s1 = StringUtils.split(arg1, ';');
                    if (s0.size() == s1.size()) {
                        return 0;
                    }
                    return (s0.size() > s1.size()) ? -1 : 1;
                } else {
                    return 1;
                }
            } else if (arg1.contains("*")) {
                return -1;
            } else {
                List<String> s0 = StringUtils.split(arg0, ';');
                List<String> s1 = StringUtils.split(arg1, ';');
                if (s0.size() == s1.size()) {
                    return 0;
                }
                return (s0.size() > s1.size()) ? -1 : 1;
            }
        }
    }

    public static interface Creator {

        Renderable create(Object source);
    }

    public static class Creators {

        public static Creator json() {
            return new Creator() {

                public Renderable create(Object source) {
                    return Json.as(source);
                }

                public String toString() {
                    return "map with " + Json.class;
                }
            };
        }

        public static Creator text() {
            return new Creator() {

                public Renderable create(Object source) {
                    return Text.with(source != null ? source.toString() : StringUtils.EMPTY);
                }

                public String toString() {
                    return "map with " + Text.class;
                }
            };
        }

        public static Creator self(final Renderable d) {
            return new Creator() {

                public Renderable create(Object source) {
                    return d;
                }

                public String toString() {
                    return "map with " + d.getClass();
                }
            };
        }
    }
}
