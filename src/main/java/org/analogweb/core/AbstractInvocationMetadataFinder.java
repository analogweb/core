package org.analogweb.core;

import java.lang.reflect.Method;

import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.RequestPathMetadata;

/**
 * @author snowgooseyk
 */
public abstract class AbstractInvocationMetadataFinder
		implements
			InvocationMetadataFinder {

	@Override
	public int getPrecedence() {
		return Integer.MIN_VALUE;
	}

	protected Cacheable cacheable(final InvocationMetadata found) {
		return new Cacheable() {

			@Override
			public Class<?> getInvocationClass() {
				return found.getInvocationClass();
			}

			@Override
			public String getMethodName() {
				return found.getMethodName();
			}

			@Override
			public Class<?>[] getArgumentTypes() {
				return found.getArgumentTypes();
			}

			@Override
			public RequestPathMetadata getDefinedPath() {
				return found.getDefinedPath();
			}

			@Override
			public InvocationMetadata getCachable() {
				return found;
			}

			@Override
			public Method resolveMethod() {
				return found.resolveMethod();
			}
		};
	}
}
