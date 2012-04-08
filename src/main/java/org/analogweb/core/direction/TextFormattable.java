package org.analogweb.core.direction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.analogweb.exception.FormatFailureException;
import org.analogweb.util.Maps;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Direction}の実装です。
 * @author snowgoose
 */
public abstract class TextFormattable extends Text {

    private static Map<String, ReplaceableFormatter> formatters = Maps.newConcurrentHashMap();
    private Object source;

    /**
     * 特定のインスタンスをテキストにフォーマットし、ストリームに書き出します。
     * インスタンスをテキストフォーマットする実装を入れ替える為に使用します。
     * @author snowgoose
     */
    public static interface ReplaceableFormatter {
        void format(OutputStream writeTo, String charset, Object source)
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
     * 現在のフォーマット可能な{@link ReplaceableFormatter}を取得します。<br/>
     * 自分のクラスに該当する{@link ReplaceableFormatter}がない場合はnullを返します。
     * その場合、自分のクラスに適切な{@link ReplaceableFormatter}を
     * {@link #render(org.analogweb.RequestContext)}を用いて登録する必要があります。
     * @return {@link ReplaceableFormatter}
     */
    protected <T extends TextFormattable> ReplaceableFormatter getFormatter() {
        return formatters.get(getClass().getCanonicalName());
    }

    /**
     * 指定した{@link ReplaceableFormatter}によって特定のフォーマットのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatter}は全ての<T>のインスタンスに適用されます。{@link ReplaceableFormatter}
     * にnullを渡すと、その{@link TextFormattable}に関連付けられている{@link ReplaceableFormatter}を
     * 破棄します。
     * @param <T> フォーマットする対象の型
     * @param textFormattable {@link TextFormattable}
     * @param formatter {@link ReplaceableFormatter}
     */
    public static synchronized <T extends TextFormattable> void replace(Class<T> textFormattable,
            ReplaceableFormatter formatter) {
        if (formatter == null) {
            formatters.remove(textFormattable.getCanonicalName());
        } else {
            formatters.put(textFormattable.getCanonicalName(), formatter);
        }
    }

    @Override
    protected void writeToStream(OutputStream out) throws IOException {
        Object toXml = getSource();
        if (toXml == null) {
            super.writeToStream(out);
            return;
        }
        ReplaceableFormatter formatter = getFormatter();
        if (formatter == null) {
            replace(getClass(), getDefaultFormatter());
            formatter = getFormatter();
        }
        formatter.format(out, getCharset(), toXml);
    }

    /**
     * デフォルトの{@link ReplaceableFormatter}によって特定のフォーマットへのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract ReplaceableFormatter getDefaultFormatter();

}
