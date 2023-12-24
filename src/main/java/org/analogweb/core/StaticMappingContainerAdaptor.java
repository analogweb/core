package org.analogweb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.analogweb.ContainerAdaptor;
import org.analogweb.util.Assertion;
import org.analogweb.util.ReflectionUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public final class StaticMappingContainerAdaptor implements ContainerAdaptor {

    private final Map<AssignableFromClassKey, Values> instanceMap;
    private static final Log log = Logs.getLog(StaticMappingContainerAdaptor.class);

    public StaticMappingContainerAdaptor() {
        instanceMap = new ConcurrentHashMap<AssignableFromClassKey, Values>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstanceOfType(Class<T> type) {
        Values values = instanceMap.get(AssignableFromClassKey.valueOf(type));
        if (values == null) {
            return null;
        }
        return (T) values.collection().iterator().next();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getInstancesOfType(Class<T> type) {
        Values values = instanceMap.get(AssignableFromClassKey.valueOf(type));
        if (values != null) {
            return new ArrayList<T>((Collection<T>) values.collection());
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    public <T> void register(Class<? extends T> concleteType) {
        Assertion.notNull(concleteType, "conclete type");
        for (Class<?> inf : ReflectionUtils.findAllImplementsInterfacesRecursivery(concleteType)) {
            register((Class<T>) inf, concleteType);
        }
        register((Class<T>) concleteType, concleteType);
    }

    public <T> void register(Class<T> requiredType, Class<? extends T> concleteType) {
        Assertion.notNull(requiredType, "instance of type");
        Assertion.notNull(concleteType, "conclete type");
        if (concleteType.isInterface()) {
            return;
        }
        T value = ReflectionUtils.getInstanceQuietly(concleteType);
        if (value == null) {
            log.log(Markers.BOOT_APPLICATION, "TB000003", concleteType, getClass().getSimpleName(), requiredType);
            return;
        }
        AssignableFromClassKey requiredTypeKey = AssignableFromClassKey.valueOf(requiredType);
        Values values = null;
        if (this.instanceMap.containsKey(requiredTypeKey)) {
            values = this.instanceMap.get(requiredTypeKey);
        } else {
            values = new Values();
        }
        values.add(value);
        log.log(Markers.BOOT_APPLICATION, "TB000002", value, getClass().getSimpleName(), requiredTypeKey);
        instanceMap.put(requiredTypeKey, values);
    }

    static class AssignableFromClassKey implements Serializable {

        private static final long serialVersionUID = 6877097524294606292L;
        private final Class<?> key;

        static AssignableFromClassKey valueOf(Class<?> key) {
            return new AssignableFromClassKey(key);
        }

        private AssignableFromClassKey(Class<?> key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object other) {
            if ((other instanceof AssignableFromClassKey) == false) {
                return false;
            }
            return this.key.isAssignableFrom(((AssignableFromClassKey) other).key);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public String toString() {
            return new StringBuilder("assignable from ").append(this.key.getCanonicalName()).toString();
        }
    }

    private static class Values implements Serializable {

        private static final long serialVersionUID = 7232656030740922324L;
        private final Set<Object> values;

        Values() {
            values = new HashSet<Object>();
        }

        void add(Object object) {
            this.values.add(object);
        }

        Collection<Object> collection() {
            return this.values;
        }
    }
}
