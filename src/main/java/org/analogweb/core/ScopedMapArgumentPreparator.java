package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.Attributes;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.ArrayUtils;

/**
 * @author snowgoose
 */
public class ScopedMapArgumentPreparator extends AbstractApplicationProcessor {

	@Override
	public Object prepareInvoke(InvocationArguments args,
			InvocationMetadata metadata, RequestContext context,
			TypeMapperContext converters, RequestValueResolvers handlers) {
		Method method = metadata.resolveMethod();
		if (method == null) {
			return NO_INTERRUPTION;
		}
		Annotation[][] argumentAnnotations = method.getParameterAnnotations();
		Class<?>[] argTypes = metadata.getArgumentTypes();
		if (ArrayUtils.isEmpty(argTypes)
				|| ArrayUtils.isEmpty(argumentAnnotations)) {
			return NO_INTERRUPTION;
		}
		for (int index = 0, limit = argTypes.length; index < limit; index++) {
			Attributes viewAttributes = AnnotationUtils.findAnnotation(
					Attributes.class, argumentAnnotations[index]);
			if (viewAttributes != null
					&& argTypes[index].getCanonicalName().equals(
							Map.class.getCanonicalName())) {
				args.putInvocationArgument(index, new ContextExtractor<Object>(
						viewAttributes.value()));
			}
		}
		return NO_INTERRUPTION;
	}

	@Override
	public void postInvoke(Object invocationResult, InvocationArguments args,
			InvocationMetadata metadata, RequestContext context,
			RequestValueResolvers handlers) {
		for (Object arg : args.asList()) {
			if (arg instanceof ContextExtractor) {
				ContextExtractor<?> scopedAttributes = (ContextExtractor<?>) arg;
				scopedAttributes.extract(context, handlers);
			}
		}
	}

	static final class ContextExtractor<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -944143676425859153L;
		private final Set<String> removedKeys = new HashSet<String>();
		private final Class<? extends AttributesHandler> handlerClass;

		ContextExtractor(Class<? extends AttributesHandler> handlerClass) {
			super();
			this.handlerClass = handlerClass;
		}

		Class<? extends AttributesHandler> getHandlerClass() {
			return this.handlerClass;
		}

		@Override
		public V remove(Object key) {
			V removed = super.remove(key);
			removedKeys.add((String) key);
			return removed;
		}

		void extract(RequestContext context, RequestValueResolvers handlers) {
			AttributesHandler handler = handlers
					.findAttributesHandler(getHandlerClass());
			if (handler != null) {
				for (java.util.Map.Entry<String, ?> entry : entrySet()) {
					handler.putAttributeValue(context, entry.getKey(),
							entry.getValue());
				}
				for (String removedKey : removedKeys) {
					handler.removeAttribute(context, removedKey);
				}
			}
		}
	}
}
