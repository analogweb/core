package org.analogweb.core.response;

import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseFormatterAware;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;

/**
 * テキストフォーマットが可能な{@link org.analogweb.Renderable}の実装です。
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

    protected final Object getSource() {
        return this.source;
    }

    /**
     * 現在のフォーマット可能な{@link ResponseFormatter}を取得します。<br/>
     * @return {@link ResponseFormatter}
     */
    protected ResponseFormatter getFormatter() {
        return this.formatter;
    }
    
    /**
          * デフォルトの{@link ResponseFormatter}によって特定のフォーマットへのレンダリングを行います。<br/>
          * この{@link ResponseFormatter}は全ての{@link TextFormattable}のインスタンスに適用されます。
     */
    protected abstract ResponseFormatter getDefaultFormatter();

	@Override
	protected ResponseEntity extractResponseEntity(
			RequestContext request, ResponseContext response) {
		Object source = getSource();
		if (source == null) {
			return super.extractResponseEntity(request, response);
		}
		ResponseFormatter formatter = getFormatter();
		if(formatter == null){
			formatter = getDefaultFormatter();
		}
		return formatter.formatAndWriteInto(request, response, getCharsetAsText(), source);
	}

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
