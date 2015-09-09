package org.analogweb.util;

import java.net.URL;
import java.util.Collection;

/**
 * Collecting　classes from ({@link URL}).
 * @author snowgoose
 */
public interface ClassCollector {

    /**
     * @param source {@link URL}
     * @param classLoader {@link ClassLoader}
     * @return All loaded {@link Class}
     */
    Collection<Class<?>> collect(URL source, ClassLoader classLoader);

    /**
     * @param packageName {@link Class}を取得する対象のパッケージ名
     * @param source {@link URL}
     * @param classLoader {@link ClassLoader}
     * @return All loaded {@link Class}
     */
    Collection<Class<?>> collect(String packageName, URL source, ClassLoader classLoader);
}
