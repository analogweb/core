package org.analogweb.core;

import java.util.Iterator;
import java.util.List;

import org.analogweb.AttributesHandlers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.TypeMapperContext;

/**
 * @author snowgoose
 */
public class InvocationChain implements Invocation {
	
	private Invocation root;
	private InvocationMetadata metadata;
	private Iterator<InvocationInterceptor> interceptors;
	private boolean notArchiveTail = true;
	private Object result ;

	public InvocationChain(Invocation root, InvocationMetadata metadata,
			Iterator<InvocationInterceptor> interceptors) {
		super();
		this.root = root;
		this.metadata = metadata;
		this.interceptors = interceptors;
	}

	@Override
	public Object invoke() {
		if(interceptors.hasNext()){
			InvocationInterceptor interceptor = interceptors.next();
			result = interceptor.onInvoke(this, metadata);
		} else {
			if(notArchiveTail){
				result = new AbstractInvocationInterceptor(){
					@Override
					public Object onInvoke(Invocation invocation,
							InvocationMetadata metadata) {
						return root.invoke();
					}
				}.onInvoke(this, metadata);
				notArchiveTail = false;
			}
		}
		return result;
	}

	@Override
	public Object getInvocationInstance() {
		return root.getInvocationInstance();
	}

	@Override
	public InvocationArguments getInvocationArguments() {
		return root.getInvocationArguments();
	}

	@Override
	public Object prepareInvoke(List<InvocationProcessor> processors,
			AttributesHandlers attributesHandlers,
			TypeMapperContext typeMapperContext) {
		return root.prepareInvoke(processors, attributesHandlers, typeMapperContext);
	}

	@Override
	public void postInvoke(List<InvocationProcessor> processors,
			Object invocationResult, AttributesHandlers attributesHandlers) {
		root.postInvoke(processors, invocationResult, attributesHandlers);
	}

	@Override
	public Object onException(List<InvocationProcessor> processors,
			Exception thrown) {
		return root.onException(processors, thrown);
	}

	@Override
	public void afterCompletion(List<InvocationProcessor> processors,
			Object invocationResult) {
		root.afterCompletion(processors, invocationResult);
	}

}
