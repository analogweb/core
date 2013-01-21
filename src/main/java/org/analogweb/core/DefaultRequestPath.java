package org.analogweb.core;

import java.net.URI;

import org.analogweb.RequestPath;
import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class DefaultRequestPath extends AbstractRequestPathMetadata implements RequestPath {

    private final ApplicationSpecifier suffix;

    private final String actualPath;
    private final String requestMethod;
    private final URI requestURI;
    private final URI baseURI;

    public DefaultRequestPath(URI baseURI, URI requestedURI, String requestMethod) {
        this.requestURI = requestedURI;
        this.baseURI = (baseURI == null) ? URI.create("/") : baseURI;
        String path = new StringBuilder().append('/')
                .append(this.baseURI.relativize(this.requestURI).getPath()).toString();
        this.actualPath = getFormattedPath(path, this.baseURI.getPath());
        Assertion.notNull(requestMethod, "Request method.");
        this.requestMethod = requestMethod.toUpperCase();
        this.suffix = extractSuffix(path);
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
