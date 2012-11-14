package org.analogweb.core.direction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletException;

import org.analogweb.Direction;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.util.IOUtils;
import org.analogweb.util.StringUtils;

/**
 * テキストをレスポンスする{@link Direction}です。<br/>
 * レスポンスにおける既定のContent-Typeは「text/plain」であり 、文字セットは「UTF-8」です。
 * 
 * @author snowgoose
 */
public class TextFormat<T extends TextFormat<T>> implements Direction {

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
        this.responseText = input;
        this.charset = charset;
        this.contentType = contentType;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends TextFormat> T with(final String responseText) {
        return (T)new TextFormat(responseText);
    }

    @SuppressWarnings("unchecked")
    public T typeAs(String contentType) {
        this.contentType = contentType;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T withCharset(String charset) {
        if (StringUtils.isNotEmpty(charset)) {
            this.charset = charset;
        }
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T  withoutCharset() {
        this.charset = StringUtils.EMPTY;
        return (T)this;
    }

    @Override
    public void render(RequestContext context) throws IOException, ServletException {
        OutputStream out = context.getResponseBody();
        Headers headers = context.getResponseHeaders();
        headers.putValue("Content-Type", getContentType());
        writeToStream(out);
    }

    protected void writeToStream(OutputStream out) throws IOException {
        IOUtils.copyQuietly(textToInputStream(getResponseText(), getCharset()), out);
    }

    protected ByteArrayInputStream textToInputStream(String text, String charset) throws IOException {
        if (StringUtils.isEmpty(text)) {
            return new ByteArrayInputStream(StringUtils.EMPTY.getBytes());
        } else if (StringUtils.isEmpty(charset)) {
            return new ByteArrayInputStream(text.getBytes());
        } else {
            return new ByteArrayInputStream(text.getBytes(charset));
        }
    }

    protected String getResponseText() {
        return this.responseText;
    }

    public String getCharset() {
        return this.charset;
    }

    public String getContentType() {
        String charset = getCharset();
        if (StringUtils.isEmpty(charset)) {
            return this.contentType;
        } else {
            return new StringBuilder(32).append(this.contentType).append("; charset=").append(getCharset()).toString();
        }
    }

    @Override
    public String toString() {
        return responseText;
    }

}
