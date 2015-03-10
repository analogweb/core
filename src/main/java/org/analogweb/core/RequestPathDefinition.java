package org.analogweb.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.StringUtils;

/**
 * Define {@link RequestPath} that enable entry-point to invoke.
 * @author snowgoose
 */
public class RequestPathDefinition extends AbstractRequestPathMetadata {

    private static final List<String> DEFAULT_METHODS = Collections.unmodifiableList(Arrays.asList(
            "GET", "POST"));
    public static final RequestPathMetadata EMPTY = new EmptyDefinePath();
    protected final String actualPath;
    protected final List<String> requestMethods;

    protected RequestPathDefinition(String path, String[] requestMethods) {
        this.actualPath = path;
        if (ArrayUtils.isNotEmpty(requestMethods)) {
            this.requestMethods = Arrays.asList(requestMethods);
        } else {
            this.requestMethods = DEFAULT_METHODS;
        }
    }

    public static RequestPathDefinition define(String root, String path) {
        return define(root, path, new String[0]);
    }

    public static RequestPathDefinition define(String root, String path, String[] requestMethods) {
        if (StringUtils.isEmpty(root)) {
            root = StringUtils.EMPTY;
        }
        if (StringUtils.isEmpty(path)) {
            throw new InvalidRequestPathException(root, path);
        }
        StringBuilder editedRoot = editRoot(root, path);
        StringBuilder editedPath = editPath(root, path);
        return new RequestPathDefinition(editedRoot.append(editedPath).toString(), requestMethods);
    }

    protected static StringBuilder editPath(String root, String path) {
        StringBuilder editedPath = new StringBuilder(path);
        if (editedPath.indexOf("/") != 0) {
            editedPath = new StringBuilder().append('/').append(path);
        }
        //        int lastIndexOfSuffix = editedPath.lastIndexOf(".");
        //        if (editedPath.lastIndexOf("/") < lastIndexOfSuffix) {
        //            editedPath = new StringBuilder(editedPath.substring(0, lastIndexOfSuffix));
        //        }
        return editedPath;
    }

    protected static StringBuilder editRoot(String root, String path) {
        StringBuilder editedRoot = new StringBuilder(root);
        if (root.indexOf("*") > 0) {
            throw new InvalidRequestPathException(root, path);
        }
        if (root.startsWith("/") == false) {
            editedRoot = new StringBuilder().append('/').append(root);
        }
        if (editedRoot.lastIndexOf("/") == editedRoot.length() - 1) {
            editedRoot = new StringBuilder(editedRoot.substring(0, editedRoot.length() - 1));
        }
        return editedRoot;
    }

    @Override
    public String getActualPath() {
        return this.actualPath;
    }

    @Override
    public List<String> getRequestMethods() {
        return this.requestMethods;
    }

    @Override
    public boolean match(RequestPath requestPath) {
        if (getActualPath().indexOf('*') > 0) {
            return wildCardMatch(requestPath.getActualPath(), getActualPath())
                    && containsRequestMethod(requestPath);
        } else {
            if (hasPlaceHolder(getActualPath())) {
                return matchPlaceHolder(requestPath.getActualPath())
                        && containsRequestMethod(requestPath);
            }
            return getActualPath().equals(requestPath.getActualPath())
                    && containsRequestMethod(requestPath);
        }
    }

    private boolean containsRequestMethod(RequestPath other) {
        boolean contains = getRequestMethods().contains(other.getRequestMethod());
        if (contains == false) {
            throw new RequestMethodUnsupportedException(this, getRequestMethods(),
                    other.getRequestMethod());
        }
        return contains;
    }

    private boolean matchPlaceHolder(String requestedPath) {
        List<String> requestedPathes = StringUtils.split(requestedPath, '/');
        List<String> actualPathes = StringUtils.split(getActualPath(), '/');
        if (requestedPathes.size() != actualPathes.size()) {
            return false;
        }
        Iterator<String> actualPathesIterator = actualPathes.iterator();
        for (String path : requestedPathes) {
            String actualPath = actualPathesIterator.next();
            if ((actualPath.startsWith("{") == false || actualPath.endsWith("}") == false)
                    && actualPath.equals(path) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPlaceHolder(String value) {
        Pattern hasPlaceHolder = Pattern.compile(".*\\{[a-zA-z0-9]*\\}.*");
        return hasPlaceHolder.matcher(value).matches();
    }

    protected boolean wildCardMatch(String text, String pattern) {
        List<String> sp = StringUtils.split(pattern, '*');
        for (int i = 0; i < sp.size(); i++) {
            String card = sp.get(i);
            // at first.
            if (i == 0 && card.startsWith("/") && text.startsWith(card) == false) {
                return false;
            }
            int idx = text.indexOf(card);
            if (idx == -1) {
                return false;
            }
            // at last.
            if (i == sp.size() - 1 && StringUtils.isNotEmpty(card) && text.endsWith(card) == false) {
                return false;
            }
            text = text.substring(idx + card.length());
        }
        return true;
    }

    @Override
    public String toString() {
        return getActualPath();
    }

    private static final class EmptyDefinePath extends RequestPathDefinition {

        private EmptyDefinePath() {
            super(StringUtils.EMPTY, new String[0]);
        }
    }
}
