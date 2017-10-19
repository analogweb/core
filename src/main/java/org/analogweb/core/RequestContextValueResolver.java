package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.Cookies;
import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestValueResolver;

/**
 * Resolve {@link RequestContext} specific instances.(like {@link RequestPath}
 * and so on.)
 * 
 * @author snowgooseyk
 */
public class RequestContextValueResolver implements RequestValueResolver {

	@Override
	public Object resolveValue(RequestContext request,
			InvocationMetadata metadata, String name, Class<?> requiredType,
			Annotation[] parameterAnnotations) {
		if (requiredType == null) {
			return null;
		}
		if (RequestPath.class.isAssignableFrom(requiredType)) {
			return request.getRequestPath();
		} else if (MediaType.class.isAssignableFrom(requiredType)) {
			return request.getContentType();
		} else if (Headers.class.isAssignableFrom(requiredType)) {
			return request.getRequestHeaders();
		} else if (Cookies.class.isAssignableFrom(requiredType)) {
			return request.getCookies();
		}
		return null;
	}
}
