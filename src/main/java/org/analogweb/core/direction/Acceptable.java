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
 * リクエストヘッダ[Accept]に適合する{@link Direction}を選択して
 * レンダリング処理を委譲する{@link Direction}の実装です。<br/>
 * 委譲する{@link Direction}は対応するメディアタイプをキーとしてマッピングされます。
 * (例えば、[text/xml]や[application/xml]をキーとして{@link Xml}がマッピング
 * されています。)[*&#47;*](全てのメディアタイプ)にはデフォルトで{@link Json}が
 * マッピングされています。複数のメディアタイプがヘッダから検出される場合は、
 * <a href="#">RFC2616 Section 14.1</a>に示される順序に従って、対応する
 * {@link Direction}を検索し、評価します。
 * (この時、品質値(q)などの付加的なパラメータは全て加味されません。)<br/>
 * 評価する対象の{@link Direction}が存在しない（マッピングされていない）場合は、
 * {@link HttpStatus#NOT_ACCEPTABLE}を返します。
 * @author snowgoose
 */
public class Acceptable implements Direction {

    private Object source;
    protected static final String ANY_TYPE = "*/*";
    protected static final Map<String, Creator> DEFAULT_MEDIA_TYPE_MAP = new HashMap<String, Creator>() {
        private static final long serialVersionUID = 1L;
        {
            put("application/json", Creators.json());
            put("application/xml", Creators.xml());
            put("text/xml", Creators.xml());
            put("text/plain", Creators.text());
            put(ANY_TYPE, Creators.json());
        }
    };
    private Map<String, Creator> mediaTypeMap;

    /**
     * {@link Acceptable}のインスタンスを生成します。
     * @param obj 委譲される{@link Direction}に使用されるオブジェクト
     * @return {@link Acceptable}
     */
    public static Acceptable as(Object obj) {
        return new Acceptable(obj);
    }

    /**
     * コンストラクタ
     * @param obj 委譲される{@link Direction}に使用されるオブジェクト
     */
    protected Acceptable(Object obj) {
        this.source = obj;
        this.mediaTypeMap = new HashMap<String, Creator>(DEFAULT_MEDIA_TYPE_MAP);
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

    /**
     * map(matchesAny,ANY_TYPE)のショートカットです。
     * @param matchesAny 全てのメディアタイプに対応する{@link Direction}
     * @return 自身のインスタンス
     */
    public Acceptable mapToAny(Direction matchesAny) {
        return map(matchesAny, ANY_TYPE);
    }

    /**
     * 指定したメディアタイプが検出された場合に、処理を委譲する{@link Direction}
     * をマップします。既に同じメディアタイプでマップされている場合は上書きされます。
     * @param matchesAny 全てのメディアタイプに対応する{@link Direction}
     * @param mediaTypesStartWith この{@link Direction}をマップする全てのメディアタイプ
     * @return 自身のインスタンス
     */
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
