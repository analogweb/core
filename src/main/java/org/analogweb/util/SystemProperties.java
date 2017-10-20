package org.analogweb.util;

import java.util.List;

/**
 * @author snowgoose
 */
public final class SystemProperties {

	public static List<String> classPathes() {
		return StringUtils.split(classPath(), pathSeparator());
	}

	public static char pathSeparator() {
		return get("path.separator").charAt(0);
	}

	public static String classPath() {
		return get("java.class.path");
	}

	public static String userDir() {
		return get("user.dir");
	}

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
