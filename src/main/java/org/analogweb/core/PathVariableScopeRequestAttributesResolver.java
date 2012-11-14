package org.analogweb.core;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMetadata;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class PathVariableScopeRequestAttributesResolver extends AbstractAttributesHandler {

    static final String VALIABLES_CACHE_KEY = PathVariableScopeRequestAttributesResolver.class
            .getCanonicalName() + "_VALIABLES_CACHE";

    @Override
    public String getScopeName() {
        return "path";
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String name, Class<?> requiredType) {
        RequestPathMetadata definedPath = metadata.getDefinedPath();
        if (hasPlaceHolder(definedPath.getActualPath())) {
            RequestPath requestedPath = requestContext.getRequestPath();
            if (definedPath.match(requestedPath)) {
                Map<String, String> pathVariables = extractPathValues(definedPath.getActualPath(),
                        requestedPath.getActualPath());
                return pathVariables.get(name);
            }
        }
        return null;
    }

    private Map<String, String> extractPathValues(String definedPath, String requestedPath) {
        List<String> definedPathes = StringUtils.split(definedPath, '/');
        List<String> requestedPathes = StringUtils.split(requestedPath, '/');
        Map<String, String> context = Maps.newEmptyHashMap();
        int index = 0;
        for (String definedPathPart : definedPathes) {
            if (definedPathPart.startsWith("{") && definedPathPart.endsWith("}")) {
                String key = StringUtils
                        .substring(definedPathPart, 1, definedPathPart.length() - 1);
                context.put(key, requestedPathes.get(index));
            }
            index++;
        }
        return context;
    }

    private boolean hasPlaceHolder(String value) {
        Pattern hasPlaceHolder = Pattern.compile(".*\\{[a-zA-z0-9]*\\}.*");
        return hasPlaceHolder.matcher(value).matches();
    }

}
