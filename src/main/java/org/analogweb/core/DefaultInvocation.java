package org.analogweb.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.analogweb.AttributesHandlers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;

/**
 * {@link Invocation}のデフォルトの実装です。
 * 
 * @author snowgoose
 */
public class DefaultInvocation implements Invocation, InvocationArguments {

	private Object invocationInstance;
	private final InvocationMetadata metadata;
	private final RequestContext requestContext;
	private final ResponseContext responseContext;
	private final TreeMap<Integer, Object> preparedArgsMap;
	private List<Object> argumentList;
	private Method method;

	public DefaultInvocation(Object invocationInstance,
			InvocationMetadata metadata, RequestContext context,
			ResponseContext responseContext) {
		this.invocationInstance = invocationInstance;
		this.metadata = metadata;
		this.requestContext = context;
		this.responseContext = responseContext;
		this.preparedArgsMap = Maps.newTreeMap();
		this.method = ReflectionUtils.getMethodQuietly(getMetadata()
				.getInvocationClass(), metadata.getMethodName(), metadata
				.getArgumentTypes());
	}

	@Override
	public Object prepareInvoke(List<InvocationProcessor> processors,
			AttributesHandlers attributesHandlers,
			TypeMapperContext typeMapperContext) {
		Object interruption = InvocationProcessor.NO_INTERRUPTION;
		for (InvocationProcessor processor : processors) {
			interruption = processor.prepareInvoke(method, this, metadata,
					requestContext, typeMapperContext, attributesHandlers);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
		}
		return interruption;
	}

	@Override
	public Object invoke() throws InvocationFailureException {
		Object[] args = asList().toArray(new Object[argumentList.size()]);
		try {
			return method.invoke(invocationInstance, args);
		} catch (IllegalArgumentException e) {
			throw new InvocationFailureException(e, metadata, args);
		} catch (IllegalAccessException e) {
			throw new InvocationFailureException(e, metadata, args);
		} catch (InvocationTargetException e) {
			Throwable th = e.getCause();
			throw new InvocationFailureException(th, metadata, args);
		}
	}

	@Override
	public void postInvoke(List<InvocationProcessor> processors,
			Object invocationResult, AttributesHandlers attributesHandlers) {
		for (InvocationProcessor processor : processors) {
			processor.postInvoke(invocationResult, this, metadata,
					requestContext, attributesHandlers);
		}
	}

	@Override
	public Object onException(List<InvocationProcessor> processors,
			Exception thrown) {
		Object interruption = InvocationProcessor.NO_INTERRUPTION;
		for (InvocationProcessor processor : processors) {
			interruption = processor.processException(thrown, requestContext,
					this, metadata);
			if (interruption != InvocationProcessor.NO_INTERRUPTION) {
				return interruption;
			}
		}
		return interruption;
	}

	@Override
	public void afterCompletion(List<InvocationProcessor> processors,
			Object invocationResult) {
		for (InvocationProcessor processor : processors) {
			processor.afterCompletion(requestContext, this, metadata,
					invocationResult);
		}
	}

	@Override
	public Object getInvocationInstance() {
		return invocationInstance;
	}

	/**
	 * エンドポイントとなるメソッドに適用される引数を取得します。<br/>
	 * キーは引数の索引であり、値は引数となるインスタンスです。
	 * @return エンドポイントとなるメソッドに適用される引数を保持する{@link Map}
	 */
	protected Map<Integer, Object> getPreparedArgs() {
		return this.preparedArgsMap;
	}

	@Override
	public void putInvocationArgument(int index, Object arg) {
		this.preparedArgsMap.put(index, arg);
	}

	protected InvocationMetadata getMetadata() {
		return metadata;
	}

	protected RequestContext getRequestContext() {
		return requestContext;
	}

	protected ResponseContext getResponseContext() {
		return this.responseContext;
	}

	@Override
	public List<Object> asList() {
		if (this.argumentList == null) {
			this.argumentList = new ArrayList<Object>();
			Map<Integer, Object> preparedArgs = getPreparedArgs();
			for (int argumentIndex = 0; argumentIndex < getMetadata()
					.getArgumentTypes().length; argumentIndex++) {
				if (preparedArgs.containsKey(argumentIndex)) {
					argumentList.add(preparedArgs.get(argumentIndex));
				} else {
					argumentList.add((Object) null);
				}
			}
		}
		return argumentList;
	}

	@Override
	public void replace(Object newInvocationInstance) {
		this.invocationInstance = newInvocationInstance;
	}

	@Override
	public InvocationArguments getInvocationArguments() {
		return this;
	}

}
