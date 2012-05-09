package org.analogweb.core.direction;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.RequestContext;
import org.analogweb.exception.FormatFailureException;
import org.analogweb.util.Maps;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Direction}の実装です。
 * @author snowgoose
 */
public abstract class TextFormattable extends Text {

    private static Map<String, ReplaceableFormatWriter> formatters = Maps.newConcurrentHashMap();
    private Object source;

    /**
     * 特定のインスタンスをテキストにフォーマットし、ストリームに書き出します。
     * インスタンスをテキストフォーマットする実装を入れ替える為に使用します。
     * @author snowgoose
     */
    public static interface ReplaceableFormatWriter {
        void write(RequestContext writeTo, String charset, Object source)
                throws FormatFailureException;
    }

    public TextFormattable() {
        super();
    }

    public TextFormattable(String input, String contentType, String charset) {
        super(input, contentType, charset);
    }

    public TextFormattable(String input) {
        super(input);
    }

    public TextFormattable(Object source) {
        this.source = source;
    }

    protected Object getSource() {
        return this.source;
    }

    /**
     * 現在のフォーマット可能な{@link ReplaceableFormatWriter}を取得します。<br/>
     * 自分のクラスに該当する{@link ReplaceableFormatWriter}がない場合はnullを返します。
     * その場合、自分のクラスに適切な{@link ReplaceableFormatWriter}を
     * {@link #render(org.analogweb.RequestContext)}を用いて登録する必要があります。
     * @return {@link ReplaceableFormatWriter}
     */
    protected <T extends TextFormattable> ReplaceableFormatWriter getFormatter() {
        return formatters.get(getClass().getCanonicalName());
    }

    /**
     * 指定した{@link ReplaceableFormatWriter}によって特定のフォーマットのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatWriter}は全ての<T>のインスタンスに適用されます。{@link ReplaceableFormatWriter}
     * にnullを渡すと、その{@link TextFormattable}に関連付けられている{@link ReplaceableFormatWriter}を
     * 破棄します。
     * @param <T> フォーマットする対象の型
     * @param textFormattable {@link TextFormattable}
     * @param formatter {@link ReplaceableFormatWriter}
     */
    public static synchronized <T extends TextFormattable> void replace(Class<T> textFormattable,
            ReplaceableFormatWriter formatter) {
        if (formatter == null) {
            formatters.remove(textFormattable.getCanonicalName());
        } else {
            formatters.put(textFormattable.getCanonicalName(), formatter);
        }
    }

    @Override
    public void render(RequestContext context) throws IOException, ServletException {
        HttpServletResponse response = context.getResponse();
        response.setContentType(getContentType());
        Object toXml = getSource();
        if (toXml == null) {
            super.writeToStream(response.getOutputStream());
            return;
        }
        ReplaceableFormatWriter writter = getFormatter();
        if (writter == null) {
            replace(getClass(), getDefaultFormatter());
            writter = getFormatter();
        }
        writter.write(context, getCharset(), toXml);
    }

    /**
     * デフォルトの{@link ReplaceableFormatWriter}によって特定のフォーマットへのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatWriter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract ReplaceableFormatWriter getDefaultFormatter();

}
