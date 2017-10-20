package org.analogweb.core;

/**
 * @author snowgoose
 */
public class InvalidRequestPathException extends ApplicationRuntimeException {

	private static final long serialVersionUID = -4471053025891886083L;
	private String invalidRootPath;
	private String invalidPath;

	public InvalidRequestPathException(String invalidRootPath,
			String invalidPath) {
		this.invalidRootPath = invalidRootPath;
		this.invalidPath = invalidPath;
	}

	public String getInvalidPath() {
		return this.invalidPath;
	}

	public String getInvalidRootPath() {
		return this.invalidRootPath;
	}
}
