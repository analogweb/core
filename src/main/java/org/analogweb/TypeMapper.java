package org.analogweb;

/**
 * @author snowgoose
 */
public interface TypeMapper extends Module {

    Object mapToType(Object from, Class<?> requiredType, String[] formats);
}
