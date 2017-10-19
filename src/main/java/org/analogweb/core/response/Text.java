package org.analogweb.core.response;

/**
 * @author snowgoose
 */
public class Text extends TextFormat<Text> {

	protected Text(String input) {
		super(input);
	}

	protected Text(String input, String contentType, String charset) {
		super(input, charset, contentType);
	}

	public static Text with(final String responseText) {
		return new Text(responseText);
	}
}
