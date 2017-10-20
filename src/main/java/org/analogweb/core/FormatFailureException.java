package org.analogweb.core;

/**
 * @author snowgoose
 */
public class FormatFailureException extends ApplicationRuntimeException {

	private static final long serialVersionUID = -9139072067367686900L;
	private String format;
	private Object formattingObject;

	public FormatFailureException(Throwable cause, Object formattingObject,
			String format) {
		super(cause);
		this.format = format;
		this.formattingObject = formattingObject;
	}

	public String getFormat() {
		return format;
	}

	public Object getFormattingObject() {
		return formattingObject;
	}
}
