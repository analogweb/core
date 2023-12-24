package org.analogweb.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgoose
 */
public class DefaultInvocation implements Invocation, InvocationArguments {

    private Object invocationInstance;
    private final InvocationMetadata metadata;
    private final RequestContext requestContext;
    private final ResponseContext responseContext;
    private final TreeMap<Integer, Object> preparedArgsMap;
    private List<Object> argumentList;

    public DefaultInvocation(Object invocationInstance, InvocationMetadata metadata, RequestContext context,
            ResponseContext responseContext) {
        this.invocationInstance = invocationInstance;
        this.metadata = metadata;
        this.requestContext = context;
        this.responseContext = responseContext;
        this.preparedArgsMap = Maps.newTreeMap();
    }

    @Override
    public Object invoke() throws InvocationFailureException {
        Object[] args = asList().toArray(new Object[argumentList.size()]);
        try {
            Method method = ReflectionUtils.getInvocationMethod(metadata);
            if (method == null) {
                throw new InvocationFailureException(new NoSuchMethodException(), metadata, args);
            }
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
    public Object getInvocationInstance() {
        return invocationInstance;
    }

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

    @Override
    public InvocationArguments getInvocationArguments() {
        return this;
    }
}
