package org.analogweb.core;

import javax.servlet.http.HttpServletRequest;

import org.analogweb.RequestPath;
import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class DefaultRequestPath extends AbstractRequestPathMetadata implements RequestPath {

    private final ApplicationSpecifier suffix;

    private final String actualPath;
    private final String requestMethod;

    public DefaultRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        this.actualPath = getFormattedPath(requestUri, request.getContextPath());
        String method = request.getMethod();
        Assertion.notNull(method, "Request method.");
        this.requestMethod = method.toUpperCase();
        this.suffix = extractSuffix(requestUri);
    }

    @Override
    public String getActualPath() {
        return actualPath;
    }

    @Override
    public boolean match(RequestPath requestPath) {
        return getActualPath().equals(requestPath.getActualPath());
    }

    private ApplicationSpecifier extractSuffix(String requestUri) {
        String uri = removeJsessionId(requestUri);
        int lastIndexOfSuffixSeparator = uri.lastIndexOf('.');
        if (uri.lastIndexOf('/') < lastIndexOfSuffixSeparator) {
            return ApplicationSpecifier.valueOf(uri.substring(lastIndexOfSuffixSeparator));
        } else {
            return ApplicationSpecifier.NONE;
        }
    }

    @Override
    public boolean pathThrowgh(String specifier) {
        return this.suffix.getSuffix().equals(specifier) == false;
    }

    public ApplicationSpecifier getSuffix() {
        return this.suffix;
    }

    private String getFormattedPath(String requestUri, String contextPath) {
        String uri = removeJsessionId(requestUri);
        return removeSuffix(uri.replaceFirst(contextPath, ""));
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
    public String toString() {
        return getActualPath();
    }

}
