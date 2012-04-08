package org.analogweb.util;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author snowgoose
 */
public final class Maps {

    public static <K, V> HashMap<K, V> newEmptyHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
        return map;
    }

}
