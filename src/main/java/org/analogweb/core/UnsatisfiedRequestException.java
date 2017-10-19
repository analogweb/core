package org.analogweb.core;

import org.analogweb.RequestPathMetadata;

/**
 * @author snowgoose
 */
public class UnsatisfiedRequestException extends ApplicationRuntimeException {

	private static final long serialVersionUID = -5701810553477314954L;
	private RequestPathMetadata metadata;

	public UnsatisfiedRequestException(RequestPathMetadata metadata) {
		this.metadata = metadata;
	}

	public RequestPathMetadata getMetadata() {
		return this.metadata;
	}
}
