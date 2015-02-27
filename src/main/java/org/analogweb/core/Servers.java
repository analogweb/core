package org.analogweb.core;

import java.net.URI;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.Server;
import org.analogweb.ServerFactory;
import org.analogweb.util.ClassUtils;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgooseyk
 */
public final class Servers {

    private static final Log log = Logs.getLog(Servers.class);

    private Servers() {
        // nop.
    }

    public static Server create(String uri, String... packageNames) {
        String names = StringUtils.join(',', packageNames);
        return create(URI.create(uri), DefaultApplicationProperties.properties(names));
    }

    public static Server create(String uri, ApplicationProperties properties) {
        return create(URI.create(uri), properties);
    }

    public static Server create(URI uri, ApplicationProperties properties) {
        return create(uri, properties,
                DefaultApplicationContext.context(Maps.<String, Object> newEmptyHashMap()),
                new WebApplication());
    }

    public static Server create(URI uri, ApplicationProperties properties,
            ApplicationContext context) {
        return create(uri, properties, context, new WebApplication());
    }

    public static Server create(URI uri, ApplicationProperties properties,
            ApplicationContext context, Application application) {
        log.log(Markers.BOOT_APPLICATION, "IB000005");
        Class<?> probablyFactoryClass = ClassUtils.forNameQuietly(ServerFactory.class.getPackage()
                .getName() + ".ServerFactoryImpl");
        if (probablyFactoryClass != null) {
            Object probablyFactory = ReflectionUtils.getInstanceQuietly(probablyFactoryClass);
            if (probablyFactory instanceof ServerFactory) {
                ServerFactory factory = (ServerFactory) probablyFactory;
                log.log(Markers.BOOT_APPLICATION, "IB000006", factory.toString());
                return factory.create(uri, properties, context, application);
            }
        }
        return createDefaultServer(uri, properties, context, application);
    }

    private static Server createDefaultServer(URI uri, ApplicationProperties properties,
            ApplicationContext context, Application application) {
        log.log(Markers.BOOT_APPLICATION, "IB000007", "Analogweb HTTP Server");
        return new DefaultServer(uri.getPort(), application, context, properties);
    }
}
