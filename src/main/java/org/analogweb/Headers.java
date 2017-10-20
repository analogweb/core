package org.analogweb;

import java.util.List;

/**
 * Request or response header.
 * 
 * @author snowgoose
 */
public interface Headers {

	/**
	 * Get header value.
	 * 
	 * @param name
	 *            Key of header value.
	 * @return header value.
	 */
	List<String> getValues(String name);

	/**
	 * Get all header keys.
	 * 
	 * @return All header keys
	 */
	List<String> getNames();

	/**
	 * Put header value.
	 * 
	 * @param name
	 *            key of header value.
	 * @param value
	 *            header value.
	 */
	void putValue(String name, String value);

	/**
	 * Test header value presence.
	 * 
	 * @param name
	 *            key of header value.
	 * @return when header value presence, returns {@code true}
	 */
	boolean contains(String name);
}
