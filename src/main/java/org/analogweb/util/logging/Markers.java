package org.analogweb.util.logging;

import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class Markers {

    public static final Marker BOOT_APPLICATION = SimpleMarker.valueOf("BOOT_APPLICATION");
    public static final Marker MONITOR_MODULES = SimpleMarker.valueOf("MONITOR_MODULES");
    public static final Marker INIT_COMPONENT = SimpleMarker.valueOf("INIT_COMPONENT");
    public static final Marker LIFECYCLE = SimpleMarker.valueOf("LIFECYCLE");
    public static final Marker VARIABLE_ACCESS = SimpleMarker.valueOf("VARIABLE_ACCESS");

    public static class SimpleMarker implements Marker {

        public static SimpleMarker valueOf(String name) {
            return new SimpleMarker(name);
        }

        private String name;

        public SimpleMarker(String name) {
            Assertion.notNull(name, "name");
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Marker) {
                return ((Marker) other).getName().equals(getName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }
    }
}
