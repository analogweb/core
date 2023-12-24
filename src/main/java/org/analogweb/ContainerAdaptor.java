package org.analogweb;

import java.util.List;

/**
 * Facade of a instance container like Spring,Guice,CDI and so on.
 *
 * @author snowgoose
 */
public interface ContainerAdaptor extends Module {

    /**
     * Obtain required type instance from container.
     *
     * @param type
     *            required type.
     *
     * @return a instance on container.
     */
    <T> T getInstanceOfType(Class<T> type);

    /**
     * Obtain required type instances from container.
     *
     * @param type
     *            required type.
     *
     * @return instances on container.
     */
    <T> List<T> getInstancesOfType(Class<T> type);
}
