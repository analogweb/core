package org.analogweb.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
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
public class DefaultInvocation implements Invocation, InvocationArguments {

    private Object invocationInstance;
    private final InvocationMetadata metadata;
    private final RequestAttributes requestAttributes;
    private final ResultAttributes resultAttributes;
    private final RequestContext requestContext;
    private final TreeMap<Integer, Object> preparedArgsMap;
    private final TypeMapperContext converters;
    private final List<InvocationProcessor> processors;
    private List<Object> argumentList;

    public DefaultInvocation(Object invocationInstance, InvocationMetadata metadata,
            RequestAttributes attributes, ResultAttributes resultAttributes,
            RequestContext context, TypeMapperContext converters,
            List<InvocationProcessor> processors) {
        this.invocationInstance = invocationInstance;
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
        Class<?> actionClass = getMetadata().getInvocationClass();
        Class<?>[] methodArgumentTypes = getMetadata().getArgumentTypes();
        Method method = ReflectionUtils.getMethodQuietly(actionClass,
                getMetadata().getMethodName(), methodArgumentTypes);
        Object interruption = InvocationProcessor.NO_INTERRUPTION;
        List<InvocationProcessor> processors = getProcessors();
        for (InvocationProcessor processor : processors) {
            interruption = processor.prepareInvoke(method, this, getMetadata(),
                    getRequestContext(), getRequestAttributes(), getConverters());
            if (interruption != InvocationProcessor.NO_INTERRUPTION) {
                return interruption;
            }
        }
        Object invocationResult = null;
        try {
            for (InvocationProcessor processor : processors) {
                interruption = processor.onInvoke(method, this, getMetadata(), this);
                if (interruption != InvocationProcessor.NO_INTERRUPTION) {
                    return interruption;
                }
            }
            List<Object> args = asList();
            invocationResult = invoke(getMetadata(), method, getInvocationInstance(),
                    args.toArray(new Object[args.size()]));
            for (InvocationProcessor processor : processors) {
                invocationResult = processor.postInvoke(invocationResult, this, getMetadata(),
                        getRequestContext(), getRequestAttributes(), getResultAttributes());
            }
        } catch (Exception e) {
            for (InvocationProcessor processor : processors) {
                interruption = processor.processException(e, getRequestContext(), this,
                        getMetadata());
                if (interruption != InvocationProcessor.NO_INTERRUPTION) {
                    return interruption;
                }
            }
            Collection<Object> args = getPreparedArgs().values();
            throw new InvocationFailureException(e, getMetadata(), args.toArray(new Object[args
                    .size()]));
        } finally {
            for (InvocationProcessor processor : processors) {
                processor.afterCompletion(getRequestContext(), this, getMetadata(),
                        invocationResult);
            }
        }
        return invocationResult;
    }

    protected Object invoke(InvocationMetadata metadata, Method method, Object instance,
            Object[] args) throws InvocationFailureException {
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

    @Override
    public List<Object> asList() {
        if (this.argumentList == null) {
            this.argumentList = new ArrayList<Object>();
            Map<Integer, Object> preparedArgs = getPreparedArgs();
            for (int argumentIndex = 0; argumentIndex < getMetadata().getArgumentTypes().length; argumentIndex++) {
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

}
