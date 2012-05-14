package org.analogweb.core.direction;

import org.analogweb.Direction;

/**
 * テキストをレスポンスする{@link Direction}です。<br/>
 * レスポンスにおける既定のContent-Typeは「text/plain」であり 、文字セットは「UTF-8」です。
 * @author snowgoose
 */
public class Text extends TextFormat<Text> {

    protected Text(String input) {
        super(input);
    }

    protected Text(String input, String contentType, String charset) {
        super(input, charset, contentType);
    }

    @SuppressWarnings("unchecked")
    public static Text with(final String responseText) {
        return new Text(responseText);
    }

}
