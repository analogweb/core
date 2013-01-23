package org.analogweb.util;

/**
 * @author snowgoose
 */
public final class SystemProperties {

    public static String tmpDir() {
        return get("java.io.tmpdir");
    }

    public static String fileSeparator() {
        return get("file.separator");
    }

    private static String get(String name) {
        return System.getProperty(name);
    }

}
