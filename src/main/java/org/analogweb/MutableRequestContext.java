package org.analogweb;

/**
 * @author snowgooseyk
 */
public interface MutableRequestContext extends RequestContext {

	void setRequestMethod(String method);

	void setRequestPath(RequestPath path);

	RequestContext unwrap();
}
