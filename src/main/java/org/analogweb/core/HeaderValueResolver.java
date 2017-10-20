package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.util.List;

import org.analogweb.Headers;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class HeaderValueResolver implements RequestValueResolver {

	@Override
	public Object resolveValue(RequestContext requestContext,
			InvocationMetadata metadatan, String name, Class<?> requiredtype,
			Annotation[] annotations) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		Headers headers = requestContext.getRequestHeaders();
		List<String> headerValues = headers.getValues(name);
		if (headerValues == null || headerValues.isEmpty()) {
			return null;
		}
		return headerValues.toArray(new String[headerValues.size()]);
	}
}
