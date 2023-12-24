package org.analogweb.core;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.RequestValueResolver;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class PathVariableValueResolver implements RequestValueResolver {

    private static final String VARIABLES_CACHE_KEY = PathVariableValueResolver.class.getName()
            + ParameterValueResolver.class.hashCode();

    @Override
    public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata, String name,
            Class<?> requiredType, Annotation[] annotations) {
        RequestPathMetadata definedPath = metadata.getDefinedPath();
        String actualPath = definedPath.getActualPath();
        if (hasPlaceHolder(actualPath) || (StringUtils.isNotEmpty(actualPath) && actualPath.contains("$<"))) {
            RequestPath requestedPath = requestContext.getRequestPath();
            if (definedPath.match(requestedPath)) {
                Map<String, String> pathVariables = extractPathValues(definedPath.getActualPath(), requestContext);
                return pathVariables.get(name);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> extractPathValues(String definedPath, RequestContext rc) {
        Object expected = rc.getAttribute(VARIABLES_CACHE_KEY);
        if (expected instanceof Map<?, ?>) {
            return (Map<String, String>) expected;
        }
        String requestedPath = rc.getRequestPath().getActualPath();
        List<String> definedPathes = StringUtils.split(definedPath, '/');
        List<String> requestedPathes = StringUtils.split(requestedPath, '/');
        Map<String, String> context = Maps.newEmptyHashMap();
        int index = 0;
        for (String definedPathPart : definedPathes) {
            if (definedPathPart.startsWith("{") && definedPathPart.endsWith("}")) {
                String key = StringUtils.substring(definedPathPart, 1, definedPathPart.length() - 1);
                context.put(key, requestedPathes.get(index));
            }
            List<String> identifiners = StringUtils.split(definedPathPart, '$');
            if (identifiners.size() == 2) {
                String pattern = identifiners.get(1);
                if (pattern.startsWith("<") && pattern.endsWith(">")) {
                    String regex = StringUtils.substring(pattern, 1, pattern.length() - 1);
                    String value = requestedPathes.get(index);
                    if (Pattern.matches(regex, value)) {
                        context.put(identifiners.get(0), value);
                    }
                }
            }
            index++;
        }
        rc.setAttribute(VARIABLES_CACHE_KEY, context);
        return context;
    }

    private boolean hasPlaceHolder(String value) {
        Pattern hasPlaceHolder = Pattern.compile(".*\\{[a-zA-z0-9]*\\}.*");
        return hasPlaceHolder.matcher(value).matches();
    }
}
