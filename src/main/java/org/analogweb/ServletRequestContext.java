package org.analogweb;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletRequestContext extends RequestContext {

    HttpServletRequest getServletRequest();
    HttpServletResponse getServletResponse();
    ServletContext getServletContext();

}
