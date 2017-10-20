package org.analogweb;

/**
 * Context of application.
 * 
 * @author snowgoose
 */
public interface ApplicationContext {

	/**
	 * Obtain application scoped attributes.
	 * 
	 * @param requiredType
	 *            Type of attribute value.
	 * @param contextKey
	 *            Key of attribute value.
	 * @return Attribute value.
	 */
	<T> T getAttribute(Class<T> requiredType, String contextKey);
}
