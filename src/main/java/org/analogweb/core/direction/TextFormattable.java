package org.analogweb.core.direction;

import java.io.IOException;

import javax.servlet.ServletException;

import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionFormatterAware;
import org.analogweb.Headers;
import org.analogweb.RequestContext;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Direction}の実装です。
 * @param <T> フォーマットする{@link TextFormattable}の型
 * @author snowgoose
 */
public abstract class TextFormattable<T extends TextFormattable<T>> extends TextFormat<T> implements
        DirectionFormatterAware<T> {

    private Object source;
    private DirectionFormatter formatter;

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
     * 現在のフォーマット可能な{@link DirectionFormatter}を取得します。<br/>
     * @return {@link DirectionFormatter}
     */
    protected DirectionFormatter getFormatter() {
        return this.formatter;
    }

    @Override
    public void render(RequestContext context) throws IOException, ServletException {
        Object toXml = getSource();
        if (toXml == null) {
            super.writeToStream(context.getResponseBody());
            return;
        }
        DirectionFormatter formatter = getFormatter();
        if (formatter == null) {
            formatter = getDefaultFormatter();
        }
        Headers headers = context.getResponseHeaders();
        headers.putValue("Content-Type", getContentType());
        formatter.formatAndWriteInto(context, getCharset(), toXml);
    }

    /**
     * デフォルトの{@link DirectionFormatter}によって特定のフォーマットへのレンダリングを行います。<br/>
     * この{@link DirectionFormatter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract DirectionFormatter getDefaultFormatter();

    /**
     * 指定した{@link DirectionFormatter}によって特定のフォーマットのレンダリングを行います。<br/>
     * 既に{@link DirectionFormatter}が指定されている場合は無視されます。
     * @param formatter {@link DirectionFormatter}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T attach(DirectionFormatter formatter) {
        if (this.formatter == null) {
            this.formatter = formatter;
        }
        return (T) this;
    }

    @Override
    public String toString() {
        DirectionFormatter f;
        return String.format("%s with %s", getClass(),
                (f = getFormatter()) == null ? "default-formatter" : f);
    }

}
