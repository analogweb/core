package org.analogweb.util;

import java.util.Collection;
import java.util.List;

/**
 * @author snowgoose
 */
public class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> aCollection) {
        return (aCollection == null || aCollection.isEmpty());
    }

    public static <T> boolean isNotEmpty(Collection<T> aCollection) {
        return isEmpty(aCollection) == false;
    }

    public static <T> T indexOf(List<T> aCollection, int index) {
        return indexOf(aCollection, index, null);
    }

    public static <T> T indexOf(List<T> aCollection, int index, T defaultValue) {
        if (isEmpty(aCollection) || index < 0 || aCollection.size() <= index) {
            return defaultValue;
        }
        return aCollection.get(index);
    }
}
