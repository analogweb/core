package org.analogweb.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
import org.analogweb.ApplicationContextResolver;
import org.analogweb.ApplicationProperties;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.exception.MissingRequiredParameterException;
import org.analogweb.exception.WebApplicationException;
import org.analogweb.util.ApplicationPropertiesHolder;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * TODO write test case.
 * @author snowgoose
 */
public class AnalogFilter implements Filter {

    private static final Log log = Logs.getLog(AnalogFilter.class);
    private Application webApplication;
    private ApplicationProperties props;
    private ServletContext servletContext;

    @Override
    public void destroy() {
        this.props = null;
        this.webApplication.dispose();
        this.webApplication = null;
        this.servletContext = null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        RequestContext context = createRequestContext(getServletContext(), request, response);

        RequestPath requestedPath = context.getRequestPath();

        log.log(Markers.LIFECYCLE, "DL000002", requestedPath);

        String specifier = webApplication.getApplicationSpecifier();
        if (requestedPath.pathThrowgh(specifier)) {
            log.log(Markers.LIFECYCLE, "DL000003", requestedPath, specifier);
            chain.doFilter(request, response);
            return;
        }
        try {
            webApplication.processRequest(requestedPath, context);
        } catch (WebApplicationException e) {
            Throwable t = e.getCause();
            if (t != null) {
                throw new ServletException(e.getCause());
            } else {
                throw new ServletException(e);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        ClassLoader classLoader = getCurrentClassLoader();
        this.webApplication = createApplication(filterConfig, classLoader);
        props = configureApplicationProperties(filterConfig);
        this.webApplication.run(createApplicationContextResolver(this.servletContext), props,
                classLoader);
    }

    protected ApplicationProperties configureApplicationProperties(final FilterConfig filterConfig) {
        return ApplicationPropertiesHolder.configure(this.webApplication,
                new ApplicationPropertiesHolder.Creator() {
                    @Override
                    public ApplicationProperties create() {
                        return new ApplicationProperties() {

                            private Collection<String> packageNames;
                            private String applicationSpecifier;
                            private String tempDirectoryPath;

                            @Override
                            public File getTempDir() {
                                if (this.tempDirectoryPath == null) {
                                    this.tempDirectoryPath = createTempDirPath(filterConfig);

                                }
                                return new File(tempDirectoryPath);
                            }

                            @Override
                            public Collection<String> getComponentPackageNames() {
                                if (this.packageNames == null) {
                                    this.packageNames = createUserDefinedPackageNames(filterConfig);
                                }
                                return this.packageNames;
                            }

                            @Override
                            public String getApplicationSpecifier() {
                                if (this.applicationSpecifier == null) {
                                    this.applicationSpecifier = createApplicationSpecifier(filterConfig);
                                }
                                return this.applicationSpecifier;
                            }
                        };
                    }

                    private Set<String> createUserDefinedPackageNames(FilterConfig filterConfig) {
                        String tokenizedRootPackageNames = filterConfig
                                .getInitParameter(Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES);
                        if (StringUtils.isNotEmpty(tokenizedRootPackageNames)) {
                            StringTokenizer tokenizer = new StringTokenizer(
                                    tokenizedRootPackageNames, ",");
                            Set<String> packageNames = new HashSet<String>();
                            while (tokenizer.hasMoreTokens()) {
                                packageNames.add(tokenizer.nextToken());
                            }
                            return packageNames;
                        } else {
                            throw new MissingRequiredParameterException(
                                    Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES);
                        }
                    }

                    private String createApplicationSpecifier(FilterConfig filterConfig) {
                        String specifier = filterConfig
                                .getInitParameter(Application.INIT_PARAMETER_APPLICATION_SPECIFIER);
                        if (StringUtils.isEmpty(specifier)) {
                            return StringUtils.EMPTY;
                        } else {
                            return specifier;
                        }
                    }

                    private String createTempDirPath(FilterConfig filterConfig) {
                        String tmpDirPath = filterConfig
                                .getInitParameter(Application.INIT_PARAMETER_APPLICATION_TEMPORARY_DIR);
                        if (StringUtils.isEmpty(tmpDirPath)) {
                            return System.getProperty("java.io.tmpdir") + "/"
                                    + WebApplication.class.getCanonicalName();
                        } else {
                            return tmpDirPath + "/" + WebApplication.class.getCanonicalName();
                        }
                    }
                });
    }

    protected RequestContext createRequestContext(ServletContext context,
            HttpServletRequest request, HttpServletResponse response) {
        return new DefaultRequestContext(request, response, context);
    }

    protected ApplicationContextResolver createApplicationContextResolver(ServletContext context) {
        return new ServletContextApplicationContextResolver(context);
    }

    protected Application createApplication(FilterConfig config, ClassLoader classLoader) {
        return new WebApplication();
    }

    protected ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected final ServletContext getServletContext() {
        return this.servletContext;
    }

}
