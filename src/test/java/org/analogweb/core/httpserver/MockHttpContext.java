package org.analogweb.core.httpserver;

import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * @author snowgoose
 */
public class MockHttpContext extends HttpContext {
    
    private String path;

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#getAuthenticator()
     */
    @Override
    public Authenticator getAuthenticator() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#getFilters()
     */
    @Override
    public List<Filter> getFilters() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#getHandler()
     */
    @Override
    public HttpHandler getHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#getServer()
     */
    @Override
    public HttpServer getServer() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#setAuthenticator(com.sun.net.httpserver.Authenticator)
     */
    @Override
    public Authenticator setAuthenticator(Authenticator arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.httpserver.HttpContext#setHandler(com.sun.net.httpserver.HttpHandler)
     */
    @Override
    public void setHandler(HttpHandler arg0) {
        // TODO Auto-generated method stub

    }

    public void setPath(String path) {
        this.path = path;
    }

}
