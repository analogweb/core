package org.analogweb.core;

import java.util.List;
import java.util.Map;

import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class RequestCookies extends EmptyCookies {

	private final String header;
	private Map<String, Cookie> map;

	public RequestCookies(String header) {
		this.header = header;
	}

	protected String getHeaderValue() {
		return this.header;
	}

	@Override
	public Cookie getCookie(String name) {
		if (map == null) {
			map = toMap(getHeaderValue());
		}
		return map.get(name);
	}

	protected Map<String, Cookie> toMap(String header) {
		List<String> splitted = StringUtils.split(header, ';');
		Map<String, Cookie> map = Maps.newEmptyHashMap();
		for (String pair : splitted) {
			int eq = pair.indexOf('=');
			String name, value;
			if (eq < 0 || (eq == header.length() - 1)) {
				name = pair;
				value = StringUtils.EMPTY;
			} else {
				name = pair.substring(0, eq).trim();
				value = pair.substring(eq + 1).trim();
			}
			map.put(name, new DefaultCookie(name, value));
		}
		return map;
	}
}
