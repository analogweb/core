package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.analogweb.AttributesHandlers;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.ResultAttributes;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.To;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.StringUtils;

/**
 * {@link To}注釈が付与された{@link org.analogweb.annotation.On} メソッドの引数に対して、
 * {@link Map}インスタンスを生成し、スコープを操作するためのインターフェースとして提供します。<br/>
 * スコープは{@link To#value()}属性に指定された値が適用されます。 適用されるメソッドの引数は{@link String}を キーとする
 * {@link Map}でなければなりません。{@link To}注釈を付与している場合でも、この条件を満たさない場合は {@link Map}
 * インスタンスの適用はされません。
 * @author snowgoose
 */
public class ScopedMapArgumentPreparator extends AbstractInvocationProcessor {

    @Override
    public Object prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, TypeMapperContext converters,
            AttributesHandlers handlers) {
        Annotation[][] argumentAnnotations = method.getParameterAnnotations();
        Class<?>[] argTypes = metadata.getArgumentTypes();
        for (int index = 0, limit = argTypes.length; index < limit; index++) {
            To viewAttributes = AnnotationUtils
                    .findAnnotation(To.class, argumentAnnotations[index]);
            if (viewAttributes != null
                    && argTypes[index].getCanonicalName().equals(Map.class.getCanonicalName())) {
                args.putInvocationArgument(index,
                        new ContextExtractor<Object>(viewAttributes.value()));
            }
        }
        return NO_INTERRUPTION;
    }

    @Override
    public Object postInvoke(Object invocationResult, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, ResultAttributes resultAttributes) {
        for (Object arg : args.asList()) {
            if (arg instanceof ContextExtractor) {
                ContextExtractor<?> scopedAttributes = (ContextExtractor<?>) arg;
                scopedAttributes.extract(context, resultAttributes);
            }
        }
        return invocationResult;
    }

    static final class ContextExtractor<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -944143676425859153L;
        private Set<String> removedKeys = new HashSet<String>();
        private String scopeName;

        ContextExtractor(String scopeName) {
            super();
            this.scopeName = StringUtils.isEmpty(scopeName) ? "request" : scopeName;
        }

        String getScopeName() {
            return this.scopeName;
        }

        @Override
        public V remove(Object key) {
            V removed = super.remove(key);
            removedKeys.add((String) key);
            return removed;
        }

        void extract(RequestContext context, ResultAttributes resultAttributes) {
            for (Entry<String, ?> entry : entrySet()) {
                resultAttributes.setValueOfQuery(context, scopeName, entry.getKey(),
                        entry.getValue());
            }
            for (String removedKey : removedKeys) {
                resultAttributes.removeValueOfQuery(context, getScopeName(), removedKey);
            }
        }

    }

}
