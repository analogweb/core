package org.analogweb.core;

import java.lang.reflect.Array;
import java.util.Collection;

import org.analogweb.ContainerAdaptor;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Assertion;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class DefaultTypeMapperContext implements TypeMapperContext {

    private static final Log log = Logs.getLog(DefaultTypeMapperContext.class);
    private TypeMapper defaultTypeMapper = new AutoTypeMapper();
    private ContainerAdaptor containerAdapter;
    
    public DefaultTypeMapperContext(ContainerAdaptor containerAdapter){
        this.containerAdapter = containerAdapter;
    }

    @Override
    public Object mapToType(Class<? extends TypeMapper> typeMapperClass, RequestContext context,
            RequestAttributes attributes, Object from, Class<?> requiredType, String[] formats) {

        Assertion.notNull(attributes, "RequestAttributes");
        Assertion.notNull(requiredType, "RequiredType");

        log.log(Markers.VARIABLE_ACCESS, "DC000001", from, requiredType, formats);

        if (requiredType.isAssignableFrom((from.getClass()))) {
            return from;
        }
        Object result = handleCollection(requiredType, from);
        if (result != null) {
            return result;
        }
        result = handleArray(requiredType, from);
        if (result != null) {
            return result;
        }
        TypeMapper typeMapper = findTypeMapper(typeMapperClass);
        if (typeMapper != null) {
            return typeMapper.mapToType(context, attributes, from, requiredType, formats);
        } else {
            return getDefaultTypeMapper().mapToType(context, attributes, from, requiredType,
                    formats);
        }
    }

    protected Object handleArray(Class<?> requiredType, Object from) {
        if (from == null) {
            return null;
        }
        Object result = null;
        if (from.getClass().isArray() && ArrayUtils.isNotEmpty((Object[]) from)
                && (result = Array.get(from, 0)) != null) {
            if (requiredType.isAssignableFrom(result.getClass())) {
                return result;
            } else if (requiredType.isArray()
                    && requiredType.getComponentType().isAssignableFrom(result.getClass())) {
                return from;
            }
        }
        return null;
    }

    protected Object handleCollection(Class<?> requiredType, Object from) {
        if (from == null) {
            return null;
        }
        if (from instanceof Collection) {
            Collection<?> fromCollection = (Collection<?>) from;
            if (Collection.class.isAssignableFrom(requiredType)) {
                return fromCollection;
            }
            Object result = null;
            if (fromCollection.isEmpty() == false
                    && requiredType.isAssignableFrom((result = fromCollection.iterator().next())
                            .getClass())) {
                return result;
            }
        }
        return null;
    }

    protected TypeMapper findTypeMapper(Class<? extends TypeMapper> clazz) {
        if (clazz == null || isDefaultTypeMapper(clazz)) {
            return null;
        }
        return getContainerAdaptor().getInstanceOfType(clazz);
    }

    protected ContainerAdaptor getContainerAdaptor(){
        return this.containerAdapter;
    }

    protected boolean isDefaultTypeMapper(Class<? extends TypeMapper> clazz) {
        return clazz.equals(TypeMapper.class);
    }

    protected TypeMapper getDefaultTypeMapper() {
        return this.defaultTypeMapper;
    }

    public void setDefaultTypeMapper(TypeMapper defaultTypeMapper) {
        this.defaultTypeMapper = defaultTypeMapper;
    }

}
