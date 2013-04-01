package org.analogweb.core.response;

import java.io.IOException;

import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseFormatterAware;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Response}の実装です。
 * @param <T> フォーマットする{@link TextFormattable}の型
 * @author snowgoose
 */
public abstract class TextFormattable<T extends TextFormattable<T>> extends TextFormat<T> implements
        ResponseFormatterAware<T> {

    private Object source;
    private ResponseFormatter formatter;

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
     * 現在のフォーマット可能な{@link ResponseFormatter}を取得します。<br/>
     * @return {@link ResponseFormatter}
     */
    protected ResponseFormatter getFormatter() {
        return this.formatter;
    }

    @Override
    public void render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        Object toXml = getSource();
        Headers headers = response.getResponseHeaders();
        if (toXml == null) {
            super.writeEntity(response);
            return;
        }
        ResponseFormatter formatter = getFormatter();
        if (formatter == null) {
            formatter = getDefaultFormatter();
        }
        headers.putValue("Content-Type", getContentType());
        formatter.formatAndWriteInto(context, response, getCharset(), toXml);
    }

    /**
     * デフォルトの{@link ResponseFormatter}によって特定のフォーマットへのレンダリングを行います。<br/>
     * この{@link ResponseFormatter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract ResponseFormatter getDefaultFormatter();

    /**
     * 指定した{@link ResponseFormatter}によって特定のフォーマットのレンダリングを行います。<br/>
     * 既に{@link ResponseFormatter}が指定されている場合は無視されます。
     * @param formatter {@link ResponseFormatter}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T attach(ResponseFormatter formatter) {
        if (this.formatter == null) {
            this.formatter = formatter;
        }
        return (T) this;
    }

    @Override
    public String toString() {
        ResponseFormatter f;
        return String.format("%s with %s", getClass(),
                (f = getFormatter()) == null ? "default-formatter" : f);
    }

}