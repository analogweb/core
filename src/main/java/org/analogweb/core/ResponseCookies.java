/**
 * 
 */
package org.analogweb.core;

import java.util.Map;

import org.analogweb.Cookies;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 *
 */
public class ResponseCookies implements Cookies {

	private final Map<String, Cookie> map;

	public ResponseCookies() {
		this.map = Maps.newEmptyHashMap();
	}

	@Override
	public Cookie getCookie(String name) {
		return map.get(name);
	}

	@Override
	public void putCookie(Cookie cookie) {
		this.map.put(cookie.getName(), cookie);
	}

	@Override
	public void putCookie(String name, Object value) {
		if (value instanceof Cookie) {
			putCookie((Cookie) value);
		} else {
			putCookie(new DefaultCookie(name, value.toString()));
		}
	}
}
