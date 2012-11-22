package org.analogweb.core;

import org.analogweb.TypeMapper;

/**
 * @author snowgoose
 */
public abstract class TypeSafeTypeMapper<F, T> implements TypeMapper {

    @Override
    @SuppressWarnings("unchecked")
    public Object mapToType(Object from, Class<?> requiredType, String[] formats) {
        return mapToTypeInternal((F) from, (Class<T>) requiredType, formats);
    }

    public T mapToTypeInternal(F from, Class<T> requiredType, String[] formats) {
        // TODO Auto-generated method stub
        return null;
    }

}
