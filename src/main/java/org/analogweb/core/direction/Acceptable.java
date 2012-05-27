package org.analogweb.core.direction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.analogweb.Direction;
import org.analogweb.RequestContext;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class Acceptable implements Direction {

    private Object source;
    private static final String ANY_TYPE = "*/*";
    private static final Map<String, Creator> DEFAULT_MEDIA_TYPE_MAP = new HashMap<String, Creator>() {
        private static final long serialVersionUID = 1L;
        {
            put("application/json", Creators.json());
            put("application/xml", Creators.xml());
            put("text/xml", Creators.xml());
            put("text/plain", Creators.text());
            put(ANY_TYPE, Creators.json());
        }
    };
    private Map<String, Creator> mediaTypeMap = new HashMap<String, Creator>(DEFAULT_MEDIA_TYPE_MAP);

    public static Acceptable as(Object obj) {
        return new Acceptable(obj);
    }

    protected Acceptable(Object obj) {
        this.source = obj;
    }

    @Override
    public void render(RequestContext context) throws IOException, ServletException {
        List<String> mediaTypes = getAcceptableMediaType(context);
        if (mediaTypes.isEmpty()) {
            HttpStatus.NOT_ACCEPTABLE.render(context);
            return;
        }
        Direction d = selectDirection(mediaTypes, getSource());
        if (d != null) {
            d.render(context);
        } else {
            HttpStatus.NOT_ACCEPTABLE.render(context);
        }
    }

    public Acceptable matchesAny(Direction matchesAny) {
        return map(matchesAny, ANY_TYPE);
    }

    public Acceptable map(Direction matches, String... mediaTypesStartWith) {
        for (String mediaTypeStartWith : mediaTypesStartWith) {
            putToMediaTypeMap(mediaTypeStartWith, Creators.self(matches));
        }
        return this;
    }

    private Direction selectDirection(List<String> mediaTypes, Object source) {
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
        HttpServletRequest request = context.getRequest();
        String acceptHeader = request.getHeader("Accept");
        if (StringUtils.isNotEmpty(acceptHeader)) {
            String[] acceptables = acceptHeader.split(",");
            List<String> list = Arrays.asList(acceptables);
            Collections.sort(list, new AcceptHeaderComparator());
            return list;
        }
        return Collections.emptyList();
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
                    return (StringUtils.trimToEmpty(arg0).startsWith("*/")) ? 1 : -1;
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
                return (s0.size() > s0.size()) ? -1 : 1;
            }
        }
    }

    public static interface Creator {
        Direction create(Object source);
    }

    public static class Creators {
        public static Creator json() {
            return new Creator() {
                public Direction create(Object source) {
                    return Json.as(source);
                }

                public String toString() {
                    return "map with " + Json.class;
                }
            };
        }

        public static Creator xml() {
            return new Creator() {
                public Direction create(Object source) {
                    return Xml.as(source);
                }

                public String toString() {
                    return "map with " + Xml.class;
                }
            };
        }

        public static Creator text() {
            return new Creator() {
                public Direction create(Object source) {
                    return Text.with(source != null ? source.toString() : StringUtils.EMPTY);
                }

                public String toString() {
                    return "map with " + Text.class;
                }
            };
        }

        public static Creator self(final Direction d) {
            return new Creator() {
                public Direction create(Object source) {
                    return d;
                }

                public String toString() {
                    return "map with " + d.getClass();
                }
            };
        }
    }

}
