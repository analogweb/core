package org.analogweb.core;

import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public class DefaultInvoker implements Invoker {

	private final TypeMapperContext converters;
	private final List<InvocationProcessor> processors;
	private final AttributesHandlers handlers;

	public DefaultInvoker(List<InvocationProcessor> processors,
			TypeMapperContext converters, AttributesHandlers handlers) {
		super();
		this.converters = converters;
		this.processors = processors;
		this.handlers = handlers;
	}

	@Override
	public Object invoke(Invocation invocation, InvocationMetadata metadata,
			RequestContext request, ResponseContext response) {
		List<InvocationProcessor> processors = getInvocationProcessors();
		AttributesHandlers attributesHandlers = getAttributesHandlers();
		TypeMapperContext typeMapperContext = getTypeMapperContext();
		InvocationArguments arguments = invocation.getInvocationArguments();
		Object interruption = invocation.prepareInvoke(processors, attributesHandlers, typeMapperContext);
		if (interruption != InvocationProcessor.NO_INTERRUPTION) {
			return interruption;
		}
		Object invocationResult = null;
		try {
			invocationResult = invocation.invoke();
			invocation.postInvoke(processors, invocationResult, attributesHandlers);
		} catch (Exception e) {
			interruption = invocation.onException(processors, e);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
			List<Object> args = arguments.asList();
			throw new InvocationFailureException(e, metadata,
					args.toArray(new Object[args.size()]));
		} finally {
			invocation.afterCompletion(processors, invocationResult);
		}
		return invocationResult;
	}

	protected TypeMapperContext getTypeMapperContext() {
		return converters;
	}

	protected List<InvocationProcessor> getInvocationProcessors() {
		return processors;
	}

	protected AttributesHandlers getAttributesHandlers() {
		return handlers;
	}

}
