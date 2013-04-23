package org.analogweb.core.response;

import java.nio.charset.Charset;
import java.util.Map;

import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.core.DefaultResponseEntity;
import org.analogweb.util.StringUtils;

/**
 * テキストをレスポンスする{@link Direction}です。<br/>
 * レスポンスにおける既定のContent-Typeは「text/plain」であり 、文字セットは「UTF-8」です。
 * 
 * @author snowgoose
 */
public class TextFormat<T extends TextFormat<T>> extends DefaultResponse implements Response {

    private static final String DEFAULT_CHARSET = Charset.defaultCharset().displayName();
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    private final String responseText;

    private String contentType = DEFAULT_CONTENT_TYPE;
    private String charset = DEFAULT_CHARSET;

    protected TextFormat() {
        this(StringUtils.EMPTY, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
    }

    protected TextFormat(String input) {
        this(input, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
    }

    protected TextFormat(String input, String contentType, String charset) {
    	super();
		this.responseText = StringUtils.isEmpty(input) ? StringUtils.EMPTY
				: input;
        this.charset = charset;
        this.contentType = contentType;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends TextFormat> T with(final String responseText) {
        return (T) new TextFormat(responseText);
    }

    @SuppressWarnings("unchecked")
    public T typeAs(String contentType) {
        this.contentType = contentType;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withCharset(String charset) {
        if (StringUtils.isNotEmpty(charset)) {
            this.charset = charset;
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withoutCharset() {
        this.charset = StringUtils.EMPTY;
        return (T) this;
    }

    @Override
	protected ResponseEntity extractResponseEntity(RequestContext request,
			ResponseContext response) {
		Charset cs = getCharset();
		if (cs == null) {
			return new DefaultResponseEntity(getResponseText());
		} else {
			return new DefaultResponseEntity(getResponseText(), cs);
		}
	}

	protected void mergeHeaders(RequestContext request,
			ResponseContext response, Map<String, String> headers,
			ResponseEntity entity) {
		String contentType = resolveContentType();
		if (StringUtils.isNotEmpty(contentType)) {
			response.getResponseHeaders().putValue("Content-Type", contentType);
		}
		super.mergeHeaders(request, response, headers, entity);
	}

	protected String getResponseText() {
        return this.responseText;
    }

	public Charset getCharset() {
		String cs = getCharsetAsText();
		if (StringUtils.isEmpty(cs)) {
			return null;
		}
		return Charset.forName(cs);
	}

    public final String getCharsetAsText() {
        return this.charset;
    }

    protected String resolveContentType() {
        String charset = getCharsetAsText();
        if (StringUtils.isEmpty(charset)) {
            return this.contentType;
        } else {
            return new StringBuilder(32).append(this.contentType).append("; charset=")
                    .append(getCharsetAsText()).toString();
        }
    }

    @Override
    public String toString() {
        return responseText;
    }

}
