package org.analogweb.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.analogweb.ContainerAdaptor;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class SingletonInstanceContainerAdaptor implements ContainerAdaptor {

    private final Map<String, Object> map = Maps.newConcurrentHashMap();

    @SuppressWarnings("unchecked")
    public <T> T getInstanceOfType(Class<T> type) {
        if (map.containsKey(type.getCanonicalName()) == false) {
            try {
                map.put(type.getCanonicalName(), type.newInstance());
            } catch (InstantiationException e) {
                throw new UnsupportedOperationException("this adaptor unsupported instanticate" + type + ".");
            } catch (IllegalAccessException e) {
                throw new UnsupportedOperationException("this adaptor unsupported instanticate" + type + ".");
            }
        }
        return (T) map.get(type.getCanonicalName());
    }

    public <T> List<T> getInstancesOfType(Class<T> type) {
        return Arrays.asList(getInstanceOfType(type));
    }

    public <T> T getInstanceOfId(String id) {
        throw new UnsupportedOperationException();
    }
}
