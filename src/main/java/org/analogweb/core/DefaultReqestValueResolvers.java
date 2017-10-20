package org.analogweb.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.util.Maps;

public class DefaultReqestValueResolvers implements RequestValueResolvers {

	protected static final Class<? extends RequestValueResolver> DEFAULT_RESOLVER_CLASS = ParameterValueResolver.class;
	private final Map<Key, RequestValueResolver> resolverMap;

	public DefaultReqestValueResolvers(
			List<? extends RequestValueResolver> resolvers) {
		this.resolverMap = Maps.newConcurrentHashMap();
		for (RequestValueResolver resolver : resolvers) {
			if (resolver != null) {
				this.resolverMap
						.put(Key.valueOf(resolver.getClass()), resolver);
			}
		}
	}

	@Override
	public RequestValueResolver findDefaultRequestValueResolver() {
		return findRequestValueResolver(getDefaultRequestValueResolverClass());
	}

	@Override
	public RequestValueResolver findRequestValueResolver(
			Class<? extends RequestValueResolver> resolverClass) {
		if (resolverClass == null) {
			return getResolverMap().get(
					Key.valueOf(getDefaultRequestValueResolverClass()));
		}
		RequestValueResolver resolver = getResolverMap().get(
				Key.valueOf(resolverClass));
		if (resolver == null) {
			return getResolverMap().get(
					Key.valueOf(getDefaultRequestValueResolverClass()));
		}
		return resolver;
	}

	@Override
	public AttributesHandler findAttributesHandler(
			Class<? extends AttributesHandler> handlerClass) {
		RequestValueResolver resolver = findRequestValueResolver(handlerClass);
		if (resolver instanceof AttributesHandler) {
			return (AttributesHandler) resolver;
		}
		return null;
	}

	@Override
	public Collection<RequestValueResolver> all() {
		return getResolverMap().values();
	}

	protected final Map<Key, RequestValueResolver> getResolverMap() {
		return this.resolverMap;
	}

	protected Class<? extends RequestValueResolver> getDefaultRequestValueResolverClass() {
		return DEFAULT_RESOLVER_CLASS;
	}

	protected static class Key implements Serializable {

		private static final long serialVersionUID = 1L;
		private int hashCode;
		private final String name;

		private Key(Class<? extends RequestValueResolver> resolverClass) {
			this.name = resolverClass.getCanonicalName();
			calculateEquality(resolverClass);
		}

		private void calculateEquality(
				Class<? extends RequestValueResolver> resolverClass) {
			Class<?> target = resolverClass;
			this.hashCode = target.hashCode();
		}

		static Key valueOf(Class<? extends RequestValueResolver> resolverClass) {
			return new Key(resolverClass);
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Key) {
				Key otherKey = (Key) other;
				if (name.equals(otherKey.name)) {
					return true;
				}
			}
			return false;
		}
	}

}
