package org.analogweb.util;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.analogweb.util.ResourceUtils.FindResourceStrategies;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class FileClassCollector extends AbstractClassCollector {

    private static final Log log = Logs.getLog(FileClassCollector.class);
    private static final String CLASS_SUFFIX = ".class";

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Class<?>> collect(final String packageName, final URL url, ClassLoader classLoader) {
        if (url == null || url.getProtocol().equals("file") == false) {
            return Collections.EMPTY_LIST;
        }
        File root = new File(url.getPath());
        Set<Class<?>> classes = new HashSet<Class<?>>();
        final File[] files = root.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            return classes;
        }
        for (File file : files) {
            final String fileName = file.getName();
            if (file.isDirectory()) {
                StringBuffer newPackageName = new StringBuffer();
                if (StringUtils.isNotEmpty(packageName)) {
                    newPackageName.append(packageName);
                    newPackageName.append(".");
                }
                newPackageName.append(fileName);
                classes.addAll(collect(newPackageName.toString(),
                        ResourceUtils.findResource(file.getPath(), Arrays.asList(FindResourceStrategies.FILE)),
                        classLoader));
            } else if (fileName.endsWith(CLASS_SUFFIX)) {
                final String shortClassName = fileName.substring(0, fileName.length() - CLASS_SUFFIX.length());
                Class<?> clazz = ClassUtils.forNameQuietly(packageName + "." + shortClassName, classLoader);
                if (clazz != null) {
                    log.log(Markers.BOOT_APPLICATION, "TB000001", clazz, this);
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }
}
