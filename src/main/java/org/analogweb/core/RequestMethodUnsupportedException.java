package org.analogweb.core;

import java.util.List;

import org.analogweb.RequestPathMetadata;

/**
 * @author snowgoose
 */
public class RequestMethodUnsupportedException
		extends
			UnsatisfiedRequestException {

	private static final long serialVersionUID = -5103029925778697441L;
	private final List<String> definedMethods;
	private final String requestedMethod;

	public RequestMethodUnsupportedException(RequestPathMetadata metadata,
			List<String> definedMethods, String requestedMethod) {
		super(metadata);
		this.definedMethods = definedMethods;
		this.requestedMethod = requestedMethod;
	}

	public List<String> getDefinedMethods() {
		return definedMethods;
	}

	public String getRequestedMethod() {
		return requestedMethod;
	}
}
