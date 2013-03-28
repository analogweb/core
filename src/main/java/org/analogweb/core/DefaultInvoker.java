package org.analogweb.core;

import java.lang.reflect.Method;
import java.util.List;

import org.analogweb.RequestValueResolvers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgoose
 */
public class DefaultInvoker implements Invoker {

	private final TypeMapperContext converters;
	private final List<InvocationProcessor> processors;
	private final List<InvocationInterceptor> interceptors;
	private final RequestValueResolvers handlers;

	public DefaultInvoker(List<InvocationProcessor> processors,List<InvocationInterceptor> interceptors,
			TypeMapperContext converters, RequestValueResolvers handlers) {
		super();
		this.converters = converters;
		this.processors = processors;
		this.interceptors = interceptors;
		this.handlers = handlers;
	}

	@Override
	public Object invoke(Invocation invocation, InvocationMetadata metadata,
			RequestContext request, ResponseContext response) {
		List<InvocationProcessor> processors = getInvocationProcessors();
		RequestValueResolvers attributesHandlers = getRequestValueResolvers();
		TypeMapperContext typeMapperContext = getTypeMapperContext();
		InvocationArguments arguments = invocation.getInvocationArguments();
		Object interruption = prepareInvoke(processors,arguments, metadata,
				request, attributesHandlers, typeMapperContext);
		if (interruption != InvocationProcessor.NO_INTERRUPTION) {
			return interruption;
		}
		Object invocationResult = null;
		try {
			invocationResult = InvocationChain.create(invocation, metadata,
					getInvocationInterceptors()).invoke();
			postInvoke(processors, invocationResult, arguments,metadata,request,attributesHandlers);
		} catch (Exception e) {
			interruption = onException(processors, e, arguments,metadata,request);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
			List<Object> args = arguments.asList();
			throw new InvocationFailureException(e, metadata,
					args.toArray(new Object[args.size()]));
		}
		return invocationResult;
	}

	protected Object prepareInvoke(List<InvocationProcessor> processors,InvocationArguments args,
			InvocationMetadata metadata, RequestContext request,
			RequestValueResolvers attributesHandlers,
			TypeMapperContext typeMapperContext) {
		Object interruption = InvocationProcessor.NO_INTERRUPTION;
		Method method = ReflectionUtils.getInvocationMethod(metadata);
		for (InvocationProcessor processor : processors) {
			interruption = processor.prepareInvoke(method, args, metadata,
					request, typeMapperContext, attributesHandlers);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
		}
		return interruption;
	}

	protected void postInvoke(List<InvocationProcessor> processors,
			Object invocationResult, InvocationArguments args,
			InvocationMetadata metadata, RequestContext request,
			RequestValueResolvers attributesHandlers) {
		for (InvocationProcessor processor : processors) {
			processor.postInvoke(invocationResult, args, metadata, request,
					attributesHandlers);
		}
	}

	protected Object onException(List<InvocationProcessor> processors,
			Exception thrown, InvocationArguments args,
			InvocationMetadata metadata, RequestContext request) {
		Object interruption = InvocationProcessor.NO_INTERRUPTION;
		for (InvocationProcessor processor : processors) {
			interruption = processor.processException(thrown, request,
					args, metadata);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
		}
		return interruption;
	}

	protected TypeMapperContext getTypeMapperContext() {
		return converters;
	}

	protected List<InvocationProcessor> getInvocationProcessors() {
		return processors;
	}

	protected RequestValueResolvers getRequestValueResolvers() {
		return handlers;
	}

	protected List<InvocationInterceptor> getInvocationInterceptors(){
		return this.interceptors;
	}
}
