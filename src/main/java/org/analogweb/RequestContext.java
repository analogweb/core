package org.analogweb;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Context per request.
 * 
 * @author snowgoose
 */
public interface RequestContext {

	Cookies getCookies();

	Headers getRequestHeaders();

	Parameters getFormParameters();

	Parameters getQueryParameters();

	Parameters getMatrixParameters();

	ReadableBuffer getRequestBody() throws IOException;

	MediaType getContentType();

	RequestPath getRequestPath();

	Locale getLocale();

	List<Locale> getLocales();

	long getContentLength();

	String getCharacterEncoding();

	String getRequestMethod();

	<T> T getAttribute(String name);

	<T> void setAttribute(String name, T value);
}
