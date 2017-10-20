package org.analogweb.core;

import org.analogweb.TypeMapper;

/**
 * @author snowgoose
 */
public abstract class TypeSafeTypeMapper<F, T> implements TypeMapper {

	@Override
	@SuppressWarnings("unchecked")
	public final Object mapToType(Object from, Class<?> requiredType,
			String[] formats) {
		return mapToTypeInternal((F) from, (Class<T>) requiredType, formats);
	}

	public abstract T mapToTypeInternal(F from, Class<T> requiredType,
			String[] formats);
}
