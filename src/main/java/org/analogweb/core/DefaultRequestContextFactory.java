package org.analogweb.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.RequestContext;
import org.analogweb.RequestContextFactory;


/**
 * @author snowgoose
 */
public class DefaultRequestContextFactory implements RequestContextFactory {

    public RequestContext createRequestContext(ServletContext context, HttpServletRequest request,
            HttpServletResponse response) {
        return new DefaultRequestContext(request, response, context);
    }

}
