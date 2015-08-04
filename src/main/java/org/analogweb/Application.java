package org.analogweb;

import java.io.IOException;
import java.util.Collection;

import org.analogweb.ResponseContext.Response;
import org.analogweb.util.ClassCollector;

/**
 * An Analogweb application.
 * @author snowgoose
 */
public interface Application extends Disposable {

    String DEFAULT_PACKAGE_NAME = Application.class.getPackage().getName();
    /**
     * {@link RequestPath} not found.
     */
    Response NOT_FOUND = ResponseContext.Response.NOT_FOUND;

    /**
     * Run application.
     * @param resolver {@link ApplicationContext}
     * @param collectors {@link ClassCollector}
     * @param props {@link ApplicationProperties}
     * @param classLoader {@link ClassLoader}
     */
    void run(ApplicationContext resolver, ApplicationProperties props,
            Collection<ClassCollector> collectors, ClassLoader classLoader);

    /**
     * Process request.
     * Before then {@link Application} should be running.
     * @param path {@link RequestPath}
     * @param context {@link RequestContext}
     * @param responseContext {@link ResponseContext}
     * @throws IOException
     * @throws WebApplicationException
     */
    Response processRequest(RequestPath path, RequestContext context,
            ResponseContext responseContext) throws IOException, WebApplicationException;

    /**
     * Obtain {@link Modules}.
     * @return {@link Modules}
     */
    Modules getModules();

    /**
     * Obtain {@link RouteRegistry}.
     * @return {@link RouteRegistry}
     */
    RouteRegistry getRouteRegistry();
}
