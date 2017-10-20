package org.analogweb.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public final class ResourceUtils {

	private static final Log log = Logs.getLog(ResourceUtils.class);
	private static final List<ResourceFinder> DEFAULT_STRATEGIES = Arrays
			.asList(new ResourceFinder[]{FindResourceStrategies.CLASSPATH,
					FindResourceStrategies.URL, FindResourceStrategies.FILE});

	public static URL findResource(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return findResource(name, DEFAULT_STRATEGIES);
	}

	public static URL findResource(String name, List<ResourceFinder> strategies) {
		URL found;
		for (ResourceFinder strategy : strategies) {
			found = strategy.find(name);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	public static List<URL> findPackageResources(String packageName,
			ClassLoader classLoader) {
		return findResources(packageName.replace('.', '/'), classLoader);
	}

	public static List<URL> findResources(String name, ClassLoader classLoader) {
		List<URL> found = new ArrayList<URL>();
		try {
			Enumeration<URL> urls = classLoader.getResources(name);
			while (urls.hasMoreElements()) {
				if (urls != null) {
					found.add(urls.nextElement());
				}
			}
		} catch (IOException e) {
			return Collections.emptyList();
		}
		return found;
	}

	public interface ResourceFinder {

		URL find(String name);
	}

	public interface FindResourceStrategies {

		static final ResourceFinder FILE = new ResourceFinder() {

			@Override
			public URL find(String filename) {
				try {
					char sep = File.separator.charAt(0);
					String file = filename.replace(sep, '/');
					if (file.charAt(0) != '/') {
						String dir = SystemProperties.userDir();
						dir = dir.replace(sep, '/') + '/';
						if (dir.charAt(0) != '/') {
							dir = "/" + dir;
						}
					}
					URL fileURL = new URL("file", null, file);
					if (new File(file).exists() == false) {
						log.log("DU000002", fileURL);
						return null;
					}
					log.log("DU000001", fileURL);
					return fileURL;
				} catch (MalformedURLException e) {
					log.log("DU000002", e);
					return null;
				}
			}
		};
		static final ResourceFinder CLASSPATH = new ResourceFinder() {

			@Override
			public URL find(String name) {
				URL resource = Thread.currentThread().getContextClassLoader()
						.getResource(name);
				if (resource != null) {
					log.log("DU000003", resource.getPath());
					return resource;
				} else {
					log.log("DU000004", name);
					return null;
				}
			}
		};
		static final ResourceFinder URL = new ResourceFinder() {

			@Override
			public URL find(String name) {
				try {
					URL url = new URL(name);
					log.log("DU000005", name);
					return url;
				} catch (MalformedURLException e) {
					log.log("DU000006", name);
					return null;
				}
			}
		};
	}
}
