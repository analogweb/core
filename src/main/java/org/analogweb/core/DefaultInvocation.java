package org.analogweb.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.InvocationProcessor.InvocationArguments;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.exception.InvocationFailureException;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;


/**
 * {@link Invocation}のデフォルトの実装です。
 * @author snowgoose
 */
public class DefaultInvocation implements Invocation {

    private final Object actionInstance;
    private final InvocationMetadata metadata;
    private final RequestAttributes requestAttributes;
    private final ResultAttributes resultAttributes;
    private final RequestContext requestContext;
    private final TreeMap<Integer, Object> preparedArgsMap;
    private final TypeMapperContext converters;
    private final List<InvocationProcessor> processors;

    public DefaultInvocation(Object actionInstance, InvocationMetadata metadata,
            RequestAttributes attributes, ResultAttributes resultAttributes,
            RequestContext context, TypeMapperContext converters,
            List<InvocationProcessor> processors) {
        this.actionInstance = actionInstance;
        this.metadata = metadata;
        this.requestAttributes = attributes;
        this.resultAttributes = resultAttributes;
        this.requestContext = context;
        this.converters = converters;
        this.processors = processors;
        this.preparedArgsMap = Maps.newTreeMap();
    }

    @Override
    public Object invoke() throws InvocationFailureException {
        Class<?> actionClass = getInvocationInstance().getClass();
        Class<?>[] methodArgumentTypes = getMetadata().getArgumentTypes();
        Method method = ReflectionUtils.getMethodQuietly(actionClass, getMetadata()
                .getMethodName(), methodArgumentTypes);
        Invocation invocation = this;
        List<InvocationProcessor> processors = getProcessors();
        for (InvocationProcessor processor : processors) {
            invocation = processor.prepareInvoke(method, invocation, getMetadata(),
                    getRequestContext(), getRequestAttributes(), getConverters());
        }
        InvocationArguments argumentList = getArguments(getPreparedArgs(), methodArgumentTypes);
        Object invocationResult = null;
        try {
            for (InvocationProcessor processor : processors) {
                processor.onInvoke(method, getMetadata(), argumentList);
            }
            invocationResult = invoke(method, getInvocationInstance(),
                    argumentList.toArray());
            for (InvocationProcessor processor : processors) {
                invocationResult = processor.postInvoke(invocationResult, invocation,
                        getMetadata(), getRequestContext(), getRequestAttributes(),
                        getResultAttributes());
            }
        } catch (Exception e) {
            for (InvocationProcessor processor : processors) {
                processor.processException(e, getRequestContext(), invocation, getMetadata());
            }
        } finally {
            for (InvocationProcessor processor : processors) {
                processor.afterCompletion(getRequestContext(), invocation, getMetadata(),
                        invocationResult);
            }
        }
        return invocationResult;
    }

    protected Object invoke(Method method, Object instance, Object[] args)
            throws InvocationFailureException {
        try {
            return method.invoke(instance, args);
        } catch (IllegalArgumentException e) {
            throw new InvocationFailureException(e, getMetadata(), args);
        } catch (IllegalAccessException e) {
            throw new InvocationFailureException(e, getMetadata(), args);
        } catch (InvocationTargetException e) {
            Throwable th = e.getCause();
            throw new InvocationFailureException(th, getMetadata(), args);
        }
    }

    protected InvocationArguments getArguments(Map<Integer, Object> preparedArgs,
            Class<?>[] methodArgumentTypes) {
        final List<Object> argumentList = new ArrayList<Object>();
        for (int argumentIndex = 0; argumentIndex < methodArgumentTypes.length; argumentIndex++) {
            if (preparedArgs.containsKey(argumentIndex)) {
                argumentList.add(preparedArgs.get(argumentIndex));
            } else {
                argumentList.add((Object) null);
            }
        }
        return new InvocationArguments() {
            @Override
            public Object[] toArray() {
                return argumentList.toArray(new Object[argumentList.size()]);
            }
            @Override
            public void set(int index, Object value) {
                if (argumentList.size() > index && argumentList.get(index) == null) {
                    argumentList.set(index, value);
                }
            }
        };
    }

    @Override
    public Object getInvocationInstance() {
        return actionInstance;
    }

    @Override
    public Map<Integer, Object> getPreparedArgs() {
        return this.preparedArgsMap;
    }

    @Override
    public void putPreparedArg(int index, Object arg) {
        this.preparedArgsMap.put(index, arg);
    }

    protected InvocationMetadata getMetadata() {
        return metadata;
    }

    protected RequestAttributes getRequestAttributes() {
        return requestAttributes;
    }

    protected ResultAttributes getResultAttributes() {
        return this.resultAttributes;
    }

    protected RequestContext getRequestContext() {
        return requestContext;
    }

    protected TypeMapperContext getConverters() {
        return converters;
    }

    protected final List<InvocationProcessor> getProcessors() {
        return processors;
    }

}
