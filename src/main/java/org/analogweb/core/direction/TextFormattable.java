package org.analogweb.core.direction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionFormatterAware;
import org.analogweb.RequestContext;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Direction}の実装です。
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
     * 自分のクラスに適切な{@link DirectionFormatter}が
     * {@link DirectionFormatters}のインスタンスに登録されている必要があります。
     * @return {@link ReplaceableFormatWriter}
     */
    protected DirectionFormatter getFormatter() {
        return this.formatter;
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
        DirectionFormatter formatter = getFormatter();
        if (formatter == null) {
            formatter = getDefaultFormatter();
        }
        formatter.formatAndWriteInto(context, getCharset(), toXml);
    }

    /**
     * デフォルトの{@link ReplaceableFormatWriter}によって特定のフォーマットへのレンダリングを行います。<br/>
     * この{@link ReplaceableFormatWriter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract DirectionFormatter getDefaultFormatter();

    /**
     * 指定した{@link DirectionFormatter}によって特定のフォーマットのレンダリングを行います。<br/>
     * @param <T> フォーマットする対象の型
     * @param formatter {@link DirectionFormatter}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T attach(DirectionFormatter formatter) {
        this.formatter = formatter;
        return (T) this;
    }

}
