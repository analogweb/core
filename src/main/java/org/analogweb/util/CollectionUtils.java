package org.analogweb.util;

import java.util.Collection;

/**
 * @author snowgoose
 */
public class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> aCollection) {
        return (aCollection == null || aCollection.isEmpty());
    }

}