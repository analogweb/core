package org.analogweb;

/**
 * @author snowgoose
 */
public interface ResponseContext {

	/**
	 * Commit response to stream.
	 * 
	 * @param context
	 *            {@link RequestContext}
	 * @param response
	 *            {@link Response}
	 */
	void commit(RequestContext context, Response response);

	/**
	 * Obtain response headers.
	 * 
	 * @return response header
	 */
	Headers getResponseHeaders();

	/**
	 * Set HTTP status code.
	 * 
	 * @param status
	 *            HTTP status code
	 */
	void setStatus(int status);

	/**
	 * Test response has completed.
	 * 
	 * @return true when response has completed.
	 */
	boolean completed();

	/**
	 * Ensure response has completed.
	 */
	void ensure();

}
