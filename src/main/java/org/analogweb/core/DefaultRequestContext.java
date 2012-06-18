package org.analogweb.core;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestAttributesFactory;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;


/**
 * @author snowgoose
 */
public class DefaultRequestContext implements RequestContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;

    public DefaultRequestContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public ServletContext getContext() {
        return servletContext;
    }

    @Override
    public RequestPath getRequestPath() {
        return new DefaultRequestPath(getRequest());
    }

    @Override
    public RequestAttributes resolveRequestAttributes(RequestAttributesFactory factory,InvocationMetadata metadata,
            Map<String, AttributesHandler> resolversMap) {
        return factory.createRequestAttributes(resolversMap,metadata);
    }

}
