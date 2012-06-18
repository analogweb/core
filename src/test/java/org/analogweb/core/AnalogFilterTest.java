package org.analogweb.core;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Application;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Direction;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.Invocation;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.Modules;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestAttributesFactory;
import org.analogweb.RequestContext;
import org.analogweb.RequestContextFactory;
import org.analogweb.RequestPathMapping;
import org.analogweb.ResultAttributes;
import org.analogweb.RequestPath;
import org.analogweb.TypeMapperContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class AnalogFilterTest {

    private AnalogFilter filter;
    private Application application;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private FilterConfig filterConfig;
    private ServletContext servletContext;
    private RequestContext requestContext;
    private RequestPath servletRequestPath;
    private InvocationMetadata metadata;
    private RequestPathMapping mapping;
    private Modules modules;
    private RequestContextFactory requestContextFactory;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        application = mock(Application.class);
        filter = new AnalogFilter() {
            @Override
            protected Application createApplication(FilterConfig config, ClassLoader loader) {
                return application;
            }
        };
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        filterConfig = mock(FilterConfig.class);
        servletContext = mock(ServletContext.class);
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        requestContext = mock(RequestContext.class);
        servletRequestPath = mock(RequestPath.class);
        metadata = mock(InvocationMetadata.class);
        mapping = mock(RequestPathMapping.class);
        modules = mock(Modules.class);
        requestContextFactory = mock(RequestContextFactory.class);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeNormaly() throws Exception {

        // specify application request.
        schenarioSpecifyApplicationRequest(false);

        // find metadata.
        schenarioFindMetadata(true);

        // invoke path oriented method.
        when(application.getModules()).thenReturn(modules);
        ContainerAdaptor provider = mock(ContainerAdaptor.class);
        when(modules.getInvocationInstanceProvider()).thenReturn(provider);
        Class targetClass = getClass();
        when(metadata.getInvocationClass()).thenReturn(targetClass);
        when(provider.getInstanceOfType(targetClass)).thenReturn(this);
        RequestAttributes requestAttributes = mock(RequestAttributes.class);
        RequestAttributesFactory factory = mock(RequestAttributesFactory.class);
        when(modules.getRequestAttributesFactory()).thenReturn(factory);
        when(requestContext.resolveRequestAttributes(eq(factory), eq(metadata),isA(Map.class))).thenReturn(
                requestAttributes);
        ResultAttributes resultAttributes = mock(ResultAttributes.class);
        when(modules.getResultAttributes()).thenReturn(resultAttributes);
        Invoker invoker = mock(Invoker.class);
        when(modules.getInvoker()).thenReturn(invoker);
        TypeMapperContext typeContext = mock(TypeMapperContext.class);
        when(modules.getTypeMapperContext()).thenReturn(typeContext);
        InvocationFactory invocationFactory = mock(InvocationFactory.class);
        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        when(modules.getInvocationProcessors()).thenReturn(processors);
        Invocation invocation = mock(Invocation.class);
        when(modules.getInvocationFactory()).thenReturn(invocationFactory);
        when(invocationFactory.createInvocation(provider, metadata, requestAttributes, resultAttributes, requestContext, typeContext, processors)).thenReturn(invocation);
        Object result = new Object();
        when(invoker.invoke(invocation, metadata, requestAttributes, resultAttributes, requestContext))
                .thenReturn(result);

        // direct result.
        DirectionResolver directionResolver = mock(DirectionResolver.class);
        when(modules.getDirectionResolver()).thenReturn(directionResolver);
        Direction direction = mock(Direction.class);
        when(directionResolver.resolve(result, metadata, requestContext)).thenReturn(direction);
        DirectionHandler directionHandler = mock(DirectionHandler.class);
        when(modules.getDirectionHandler()).thenReturn(directionHandler);
        doNothing().when(directionHandler).handleResult(direction, null, requestContext,
                requestAttributes);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        verify(directionHandler).handleResult(direction, null,requestContext, requestAttributes);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithException() throws Exception {

        thrown.expect(ServletException.class);

        // specify application request.
        schenarioSpecifyApplicationRequest(false);

        // find metadata.
        schenarioFindMetadata(true);

        // invoke path oriented method.
        when(application.getModules()).thenReturn(modules);
        ContainerAdaptor provider = mock(ContainerAdaptor.class);
        when(modules.getInvocationInstanceProvider()).thenReturn(provider);
        Class targetClass = getClass();
        when(metadata.getInvocationClass()).thenReturn(targetClass);
        when(provider.getInstanceOfType(targetClass)).thenReturn(this);
        RequestAttributes requestAttributes = mock(RequestAttributes.class);
        RequestAttributesFactory factory = mock(RequestAttributesFactory.class);
        when(modules.getRequestAttributesFactory()).thenReturn(factory);
        when(requestContext.resolveRequestAttributes(eq(factory), eq(metadata), isA(Map.class))).thenReturn(
                requestAttributes);
        ResultAttributes resultAttributes = mock(ResultAttributes.class);
        when(modules.getResultAttributes()).thenReturn(resultAttributes);
        Invoker invoker = mock(Invoker.class);
        when(modules.getInvoker()).thenReturn(invoker);
        IllegalStateException ex = new IllegalStateException();
        TypeMapperContext typeContext = mock(TypeMapperContext.class);
        when(modules.getTypeMapperContext()).thenReturn(typeContext);
        InvocationFactory invocationFactory = mock(InvocationFactory.class);
        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        when(modules.getInvocationProcessors()).thenReturn(processors);
        Invocation invocation = mock(Invocation.class);
        when(modules.getInvocationFactory()).thenReturn(invocationFactory);
        when(invocationFactory.createInvocation(provider, metadata, requestAttributes, resultAttributes, requestContext, typeContext, processors)).thenReturn(invocation);
        when(invoker.invoke(invocation, metadata, requestAttributes, resultAttributes, requestContext))
                .thenThrow(ex);

        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
        when(modules.getExceptionHandler()).thenReturn(exceptionHandler);
        when(exceptionHandler.handleException(ex)).thenThrow(new ServletException(ex));

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        verify(exceptionHandler).handleException(ex);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testInvokeWithExceptionResult() throws Exception {

        // specify application request.
        schenarioSpecifyApplicationRequest(false);

        // find metadata.
        schenarioFindMetadata(true);

        // invoke path oriented method.
        when(application.getModules()).thenReturn(modules);
        ContainerAdaptor provider = mock(ContainerAdaptor.class);
        when(modules.getInvocationInstanceProvider()).thenReturn(provider);
        Class targetClass = getClass();
        when(metadata.getInvocationClass()).thenReturn(targetClass);
        when(provider.getInstanceOfType(targetClass)).thenReturn(this);
        RequestAttributes requestAttributes = mock(RequestAttributes.class);
        RequestAttributesFactory factory = mock(RequestAttributesFactory.class);
        when(modules.getRequestAttributesFactory()).thenReturn(factory);
        when(requestContext.resolveRequestAttributes(eq(factory), eq(metadata), isA(Map.class))).thenReturn(
                requestAttributes);
        ResultAttributes resultAttributes = mock(ResultAttributes.class);
        when(modules.getResultAttributes()).thenReturn(resultAttributes);
        Invoker invoker = mock(Invoker.class);
        when(modules.getInvoker()).thenReturn(invoker);
        IllegalStateException ex = new IllegalStateException();
        TypeMapperContext typeContext = mock(TypeMapperContext.class);
        when(modules.getTypeMapperContext()).thenReturn(typeContext);
        InvocationFactory invocationFactory = mock(InvocationFactory.class);
        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        when(modules.getInvocationProcessors()).thenReturn(processors);
        Invocation invocation = mock(Invocation.class);
        when(modules.getInvocationFactory()).thenReturn(invocationFactory);
        when(invocationFactory.createInvocation(provider, metadata, requestAttributes, resultAttributes, requestContext, typeContext, processors)).thenReturn(invocation);
        when(invoker.invoke(invocation, metadata, requestAttributes, resultAttributes, requestContext))
                .thenThrow(ex);

        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
        when(modules.getExceptionHandler()).thenReturn(exceptionHandler);
        Direction exceptionHandlingResult = mock(Direction.class);
        when(exceptionHandler.handleException(ex)).thenReturn(exceptionHandlingResult);

        // direct result.
        DirectionResolver directionResolver = mock(DirectionResolver.class);
        when(modules.getDirectionResolver()).thenReturn(directionResolver);
        when(directionResolver.resolve(exceptionHandlingResult, metadata, requestContext))
                .thenReturn(exceptionHandlingResult);
        DirectionHandler directionHandler = mock(DirectionHandler.class);
        when(modules.getDirectionHandler()).thenReturn(directionHandler);
        doNothing().when(directionHandler).handleResult(exceptionHandlingResult, null,
                requestContext, requestAttributes);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        verify(exceptionHandler).handleException(ex);
        verify(filterChain).doFilter(request, response);
        verify(directionHandler).handleResult(exceptionHandlingResult, null, requestContext,
                requestAttributes);
    }

    @Test
    public void testPathThrowgh() throws Exception {

        schenarioSpecifyApplicationRequest(true);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testMetadataNotFound() throws Exception {
        // specify application request.

        schenarioSpecifyApplicationRequest(false);

        schenarioFindMetadata(false);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDestroy() throws Exception {

        doNothing().when(application).dispose();
        filter.init(filterConfig);
        filter.destroy();
        verify(application).dispose();
    }

    private void schenarioSpecifyApplicationRequest(boolean pathThrowgh) {
        when(application.getModules()).thenReturn(modules);
        when(modules.getRequestContextFactory()).thenReturn(requestContextFactory);
        when(requestContextFactory.createRequestContext(servletContext, request, response))
                .thenReturn(requestContext);
        servletRequestPath = mock(RequestPath.class);
        when(requestContext.getRequestedPath()).thenReturn(servletRequestPath);
        String suffix = "";
        when(application.getApplicationSpecifier()).thenReturn(suffix);
        when(servletRequestPath.pathThrowgh(suffix)).thenReturn(pathThrowgh);
    }

    private void schenarioFindMetadata(boolean metadataFound) {
        when(application.getRequestPathMapping()).thenReturn(mapping);
        if (metadataFound) {
            when(mapping.getActionMethodMetadata(servletRequestPath)).thenReturn(metadata);
        } else {
            when(mapping.getActionMethodMetadata(servletRequestPath)).thenReturn(null);
        }
    }

}
