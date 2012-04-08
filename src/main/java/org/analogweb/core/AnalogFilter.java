package org.analogweb.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.analogweb.Application;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Direction;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.Modules;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.RequestPathMapping;
import org.analogweb.ResultAttributes;
import org.analogweb.ServletRequestPathMetadata;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;


/**
 * @author snowgoose
 */
public class AnalogFilter implements Filter {

    private static final Log log = Logs.getLog(AnalogFilter.class);
    private Application webApplication;
    private ServletContext servletContext;

    @Override
    public void destroy() {
        this.webApplication.dispose();
        this.webApplication = null;
        this.servletContext = null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Modules modules = webApplication.getModules();

        RequestContext context = modules.getRequestContextFactory().createRequestContext(
                getServletContext(), request, response);

        ServletRequestPathMetadata requestedPath = context.getRequestedPath();

        log.log(Markers.LIFECYCLE, "DL000002", requestedPath);

        String specifier = webApplication.getApplicationSpecifier();
        if (requestedPath.pathThrowgh(specifier)) {
            log.log(Markers.LIFECYCLE, "DL000003", requestedPath, specifier);
            chain.doFilter(request, response);
            return;
        }

        RequestPathMapping mapping = webApplication.getRequestPathMapping();
        log.log(Markers.LIFECYCLE, "DL000004", requestedPath);
        InvocationMetadata metadata = mapping.getActionMethodMetadata(requestedPath);
        if (metadata == null) {
            log.log(Markers.LIFECYCLE, "DL000005", requestedPath);
            chain.doFilter(request, response);
            return;
        }

        log.log(Markers.LIFECYCLE, "DL000006", requestedPath, metadata);
        try {
            ContainerAdaptor invocationInstances = modules.getInvocationInstanceProvider();

            RequestAttributes attributes = context.resolveRequestAttributes(
                    modules.getRequestAttributesFactory(), metadata,
                    modules.getAttributesHandlersMap());

            ResultAttributes resultAttributes = modules.getResultAttributes();
            
            Invocation invocation = modules.getInvocationFactory().createActionInvocation(
                    invocationInstances, metadata, attributes, resultAttributes, context,
                    modules.getTypeMapperContext(), modules.getInvocationProcessors());

            Object invocationResult = modules.getInvoker().invoke(invocation, metadata, attributes,
                    resultAttributes, context);

            log.log(Markers.LIFECYCLE, "DL000007", invocation.getInvocationInstance(), invocationResult);

            DirectionResolver resultResolver = modules.getDirectionResolver();
            Direction result = resultResolver.resolve(invocationResult, metadata, context);
            log.log(Markers.LIFECYCLE, "DL000008",invocationResult, result);

            DirectionHandler resultHandler = modules.getDirectionHandler();
            resultHandler.handleResult(result, context, attributes);
        } catch (Exception e) {
            ExceptionHandler handler = modules.getExceptionHandler();
            log.log(Markers.LIFECYCLE, "DL000009", e, handler);
            handler.handleException(e);
            chain.doFilter(request, response);
            return;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        this.webApplication = createApplication(filterConfig, getCurrentClassLoader());
    }

    protected Application createApplication(FilterConfig config, ClassLoader classLoader) {
        return new WebApplication(config, classLoader);
    }

    protected ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected final ServletContext getServletContext() {
        return this.servletContext;
    }

}
