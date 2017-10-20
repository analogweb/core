package org.analogweb.util;

import java.net.URL;
import java.util.Collection;

/**
 * @see ClassCollector
 * @author snowgoose
 */
public abstract class AbstractClassCollector implements ClassCollector {

	@Override
	public Collection<Class<?>> collect(URL source, ClassLoader classLoader) {
		return collect(StringUtils.EMPTY, source, classLoader);
	}
}
