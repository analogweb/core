package org.analogweb;

/**
 * @author snowgoose
 */
public interface AttributesHandler extends RequestValueResolver {

	void putAttributeValue(RequestContext requestContext, String query,
			Object value);

	void removeAttribute(RequestContext requestContext, String query);
}
