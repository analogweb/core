package org.analogweb.core;

import java.net.URI;

import org.analogweb.RequestPath;
import org.analogweb.util.Assertion;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class DefaultRequestPath extends AbstractRequestPathMetadata implements RequestPath {

    private final String actualPath;
    private final String requestMethod;
    private final URI requestURI;
    private final URI baseURI;

    public DefaultRequestPath(URI baseURI, URI requestedURI, String requestMethod) {
        this.requestURI = requestedURI;
        this.baseURI = (baseURI == null) ? URI.create("/") : baseURI;
        String path = this.baseURI.relativize(this.requestURI).getPath();
        if (StringUtils.charAt(0, path) != '/') {
            path = '/' + path;
        }
        this.actualPath = getFormattedPath(path, this.baseURI.getPath());
        Assertion.notNull(requestMethod, "Request method.");
        this.requestMethod = requestMethod.toUpperCase();
    }

    @Override
    public String getActualPath() {
        return actualPath;
    }

    @Override
    public boolean match(RequestPath requestPath) {
        return getActualPath().equals(requestPath.getActualPath());
    }

    @Override
    @Deprecated
    public boolean pathThrowgh(String specifier) {
        return false;
    }

    private String getFormattedPath(String requestUri, String contextPath) {
        String uri = removeJsessionId(requestUri);
        return removeSuffix(uri);
    }

    private String removeJsessionId(String uri) {
        int indexOfSessionIdSeparator = uri.indexOf(";");
        if (indexOfSessionIdSeparator == -1) {
            return uri;
        } else {
            return uri.substring(0, indexOfSessionIdSeparator);
        }
    }

    private String removeSuffix(String uri) {
        int lastIndexOfSuffixSeparator = uri.lastIndexOf('.');
        if (uri.lastIndexOf('/') < lastIndexOfSuffixSeparator) {
            return uri.substring(0, lastIndexOfSuffixSeparator);
        }
        return uri;
    }

    @Override
    public String getMethod() {
        return requestMethod;
    }

    @Override
    public URI getRequestURI() {
        return this.requestURI;
    }

    @Override
    public String toString() {
        return getActualPath();
    }

    @Override
    public URI getBaseURI() {
        return this.baseURI;
    }
}
