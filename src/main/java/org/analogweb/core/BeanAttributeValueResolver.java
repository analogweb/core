package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.analogweb.InvocationMetadata;
import org.analogweb.Modules;
import org.analogweb.ModulesAware;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.Resolver;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.ReflectionUtils;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class BeanAttributeValueResolver implements RequestValueResolver, ModulesAware {

    private RequestValueResolvers resolvers;
    private TypeMapperContext converters;

    @Override
    public Object resolveValue(RequestContext context, InvocationMetadata metadata, String key,
            Class<?> requiredType, Annotation[] parameterAnnotations) {
        Object beanInstance = instanticate(requiredType,
                AnnotationUtils.findAnnotation(Resolver.class, parameterAnnotations), metadata,
                context, getRequestValueResolvers(), parameterAnnotations);
        if (beanInstance != null) {
            for (Field field : requiredType.getDeclaredFields()) {
                Object convertedValue = AnnotatedArguments.resolveArguent(field.getName(),
                        field.getAnnotations(), field.getType(), context, metadata,
                        getTypeMapperContext(), getRequestValueResolvers());
                if (convertedValue != null) {
                    ReflectionUtils.writeValueToField(field, beanInstance, convertedValue);
                }
            }
        }
        return beanInstance;
    }

    protected final TypeMapperContext getTypeMapperContext() {
        return this.converters;
    }

    protected final RequestValueResolvers getRequestValueResolvers() {
        return this.resolvers;
    }

    private Object instanticate(Class<?> clazz, Resolver resolverAnn, InvocationMetadata metadata,
            RequestContext context, RequestValueResolvers resolvers,
            Annotation[] parameterAnnotations) {
        if (resolverAnn != null) {
            Class<? extends RequestValueResolver> resolverClass = resolverAnn.value();
            RequestValueResolver resolver = resolvers.findRequestValueResolver(resolverClass);
            if (resolver != null
            // Cyclic reference limitation.
                    && BeanAttributeValueResolver.class.equals(resolver.getClass()) == false) {
                return resolver.resolveValue(context, metadata, StringUtils.EMPTY, clazz,
                        parameterAnnotations);
            }
        }
        return ReflectionUtils.getInstanceQuietly(clazz);
    }

    @Override
    public void dispose() {
        this.converters = null;
        this.resolvers = null;
    }

    @Override
    public void setModules(Modules modules) {
        this.converters = modules.getTypeMapperContext();
        this.resolvers = modules.getRequestValueResolvers();
    }
}
