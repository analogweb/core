package org.analogweb.core;

import org.analogweb.ExceptionMapper;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgooseyk
 */
public abstract class TypeAssignableFromExceptionMapper<T extends Throwable> implements
        ExceptionMapper {

    @Override
    public boolean isMatch(Throwable throwable) {
        Class<?> clazz = ((Class<?>) ReflectionUtils.findParameterizedType(getClass())
                .getActualTypeArguments()[0]);
        if (clazz == null) {
            return false;
        }
        return clazz.isAssignableFrom(throwable.getClass());
    }
}
