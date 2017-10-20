package org.analogweb;

/**
 * @author snowgoose
 */
public interface TypeMapperContext extends Module {

	<T> T mapToType(Class<? extends TypeMapper> typeMapperClass, Object from,
			Class<T> requiredType, String[] mappingFormats);
}
