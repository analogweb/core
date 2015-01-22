package org.analogweb.core;

import java.net.URI;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.Server;
import org.analogweb.ServerFactory;
import org.analogweb.core.httpserver.AnalogHandler;
import org.analogweb.core.httpserver.HttpServers;
import org.analogweb.util.ClassUtils;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;
import org.analogweb.util.StringUtils;

/**
 * @author snowgooseyk
 */
public final class Servers {

    private Servers() {
        // nop.
    }

    public static Server create(String uri, String... packageNames) {
        String names = StringUtils.join(',', packageNames);
        return create(URI.create(uri), DefaultApplicationProperties.properties(names));
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
        Class<?> probablyFactoryClass = ClassUtils.forNameQuietly(ServerFactory.class.getPackage()
                .getName() + ".ServerFactoryImpl");
        if (probablyFactoryClass != null) {
            Object probablyFactory = ReflectionUtils.getInstanceQuietly(probablyFactoryClass);
            if (probablyFactory instanceof ServerFactory) {
                ServerFactory factory = (ServerFactory) probablyFactory;
                return factory.create(uri, properties, context, application);
            }
        }
        return createDefaultServer(uri, properties, context, application);
    }

    private static Server createDefaultServer(URI uri, ApplicationProperties properties,
            ApplicationContext context, Application application) {
        return HttpServers.create(uri, new AnalogHandler(application, context, properties));
    }
}
