package org.analogweb.util;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class JarClassCollector extends AbstractClassCollector {

	private static final Log log = Logs.getLog(JarClassCollector.class);
	private static final String CLASS_SUFFIX = ".class";

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Class<?>> collect(String packageName, URL source,
			ClassLoader classLoader) {
		Assertion.notNull(classLoader, ClassLoader.class.getName());
		if (source == null || source.getProtocol().equals("jar") == false) {
			return Collections.EMPTY_LIST;
		}
		JarFile jarFile;
		try {
			JarURLConnection connection = (JarURLConnection) source
					.openConnection();
			jarFile = connection.getJarFile();
		} catch (IOException ignore) {
			return Collections.emptyList();
		}
		Set<Class<?>> collectedClasses = new HashSet<Class<?>>();
		final Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			final JarEntry entry = enumeration.nextElement();
			final String entryName = entry.getName().replace('\\', '/');
			if (entryName.endsWith(CLASS_SUFFIX)) {
				final String className = entryName.substring(0,
						entryName.length() - CLASS_SUFFIX.length()).replace(
						'/', '.');
				final int pos = className.lastIndexOf('.');
				final String detectedPackageName = (pos == -1)
						? null
						: className.substring(0, pos);
				final String shortClassName = (pos == -1)
						? className
						: className.substring(pos + 1);
				if (detectedPackageName != null
						&& (StringUtils.isEmpty(packageName) || detectedPackageName
								.contains(packageName))) {
					Class<?> clazz = ClassUtils.forNameQuietly(
							detectedPackageName + "." + shortClassName,
							classLoader);
					log.log(Markers.BOOT_APPLICATION, "TB000001", clazz, this);
					collectedClasses.add(clazz);
				}
			}
		}
		return collectedClasses;
	}
}
