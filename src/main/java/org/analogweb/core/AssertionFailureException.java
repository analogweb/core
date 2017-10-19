package org.analogweb.core;

/**
 * @author snowgoose
 */
public class AssertionFailureException extends ApplicationRuntimeException {

	private static final long serialVersionUID = 7617581467229787706L;
	private final String requiredName;

	public AssertionFailureException(String requiredName) {
		super((String) null);
		this.requiredName = requiredName;
	}

	public String getRequiredName() {
		return this.requiredName;
	}
}
