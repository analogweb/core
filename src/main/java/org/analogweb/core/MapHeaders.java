package org.analogweb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.analogweb.Headers;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class MapHeaders implements Headers {

	private final Map<String, List<String>> source;

	public MapHeaders() {
		this(Maps.<String, List<String>> newEmptyHashMap());
	}

	public MapHeaders(Map<String, List<String>> source) {
		this.source = source;
	}

	@Override
	public boolean contains(String name) {
		return this.source.containsKey(name);
	}

	@Override
	public List<String> getNames() {
		return new ArrayList<String>(this.source.keySet());
	}

	@Override
	public List<String> getValues(String name) {
		List<String> list = this.source.get(name);
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	@Override
	public void putValue(String name, String value) {
		List<String> values = getValues(name);
		if (CollectionUtils.isEmpty(values)) {
			values = new LinkedList<String>();
		}
		values.add(value);
		this.source.put(name, values);
	}

	public Map<String, List<String>> toMap() {
		return this.source;
	}
}
