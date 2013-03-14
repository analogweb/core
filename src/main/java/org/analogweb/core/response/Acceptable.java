package org.analogweb.core.response;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Response;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.MediaTypes;
import org.analogweb.WebApplicationException;
import org.analogweb.util.StringUtils;

/**
 * リクエストヘッダ[Accept]に適合する{@link Response}を選択して
 * レンダリング処理を委譲する{@link Response}の実装です。<br/>
 * 委譲する{@link Response}は対応するメディアタイプをキーとしてマッピングされます。
 * (例えば、[text/xml]や[application/xml]をキーとして{@link Xml}がマッピング
 * されています。)[*&#47;*](全てのメディアタイプ)にはデフォルトで{@link Json}が
 * マッピングされています。複数のメディアタイプがヘッダから検出される場合は、
 * <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC2616 Section 14.1</a>
 * に示される順序に従って、対応する{@link Response}を検索し、評価します。
 * (この時、品質値(q)などの付加的なパラメータは全て加味されません。)<br/>
 * 評価する対象の{@link Response}が存在しない（マッピングされていない）場合は、
 * {@link HttpStatus#NOT_ACCEPTABLE}を返します。
 * @author snowgoose
 */
public class Acceptable implements Response {

    private Object source;
    protected static final Map<String, Creator> DEFAULT_MEDIA_TYPE_MAP = new HashMap<String, Creator>() {
        private static final long serialVersionUID = 1L;
        {
            put(MediaTypes.APPLICATION_JSON, Creators.json());
            put(MediaTypes.APPLICATION_XML, Creators.xml());
            put(MediaTypes.TEXT_XML, Creators.xml());
            put(MediaTypes.WILDCARD, Creators.json());
        }
    };
    private Map<String, Creator> mediaTypeMap;

    /**
     * {@link Acceptable}のインスタンスを生成します。
     * @param obj 委譲される{@link Response}に使用されるオブジェクト
     * @return {@link Acceptable}
     */
    public static Acceptable as(Object obj) {
        return new Acceptable(obj);
    }

    /**
     * コンストラクタ
     * @param obj 委譲される{@link Response}に使用されるオブジェクト
     */
    protected Acceptable(Object obj) {
        this.source = obj;
        this.mediaTypeMap = new HashMap<String, Creator>(DEFAULT_MEDIA_TYPE_MAP);
    }

    @Override
    public void render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        List<String> mediaTypes = getAcceptableMediaType(context);
        if (mediaTypes.isEmpty()) {
            HttpStatus.NOT_ACCEPTABLE.render(context, response);
            return;
        }
        Response d = selectResponse(mediaTypes, getSource());
        if (d == null) {
            HttpStatus.NOT_ACCEPTABLE.render(context, response);
            return;
        }
        d.render(context, response);
    }

    /**
     * map(matchesAny,ANY_TYPE)のショートカットです。
     * @param matchesAny 全てのメディアタイプに対応する{@link Response}
     * @return 自身のインスタンス
     */
    public Acceptable mapToAny(Response matchesAny) {
        return map(matchesAny, MediaTypes.WILDCARD);
    }

    /**
     * 指定したメディアタイプが検出された場合に、処理を委譲する{@link Response}
     * をマップします。既に同じメディアタイプでマップされている場合は上書きされます。
     * @param matches 指定したメディアタイプに対応する{@link Response}
     * @param mediaTypesStartWith この{@link Response}をマップする全てのメディアタイプ
     * @return 自身のインスタンス
     */
    public Acceptable map(Response matches, String... mediaTypesStartWith) {
        for (String mediaTypeStartWith : mediaTypesStartWith) {
            putToMediaTypeMap(mediaTypeStartWith, Creators.self(matches));
        }
        return this;
    }

    private Response selectResponse(List<String> mediaTypes, Object source) {
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
        Response create(Object source);
    }

    public static class Creators {
        public static Creator json() {
            return new Creator() {
                public Response create(Object source) {
                    return Json.as(source);
                }

                public String toString() {
                    return "map with " + Json.class;
                }
            };
        }

        public static Creator xml() {
            return new Creator() {
                public Response create(Object source) {
                    return Xml.as(source);
                }

                public String toString() {
                    return "map with " + Xml.class;
                }
            };
        }

        public static Creator text() {
            return new Creator() {
                public Response create(Object source) {
                    return Text.with(source != null ? source.toString() : StringUtils.EMPTY);
                }

                public String toString() {
                    return "map with " + Text.class;
                }
            };
        }

        public static Creator self(final Response d) {
            return new Creator() {
                public Response create(Object source) {
                    return d;
                }

                public String toString() {
                    return "map with " + d.getClass();
                }
            };
        }
    }

}
