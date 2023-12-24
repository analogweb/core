package org.analogweb;

import java.net.URI;

/**
 * @author snowgooseyk
 */
public interface ServerFactory {

    Server create(URI uri, ApplicationProperties properties, ApplicationContext context, Application application);
}
