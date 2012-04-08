package org.analogweb.util;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author snowgoose
 */
public interface ClassCollector {

    <T> Collection<Class<T>> collect(String packageName, URL source, ClassLoader classLoader);

    public static final List<ClassCollector> DEFAULT_COLLECTORS = Collections
            .unmodifiableList(Arrays.asList(new FileClassCollector(), new JarClassCollector()));

}
