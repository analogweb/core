package org.analogweb.core;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
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
    @SuppressWarnings("unchecked")
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String name) {
        Object variables = requestContext.getRequest().getAttribute(VALIABLES_CACHE_KEY);
        if(variables instanceof Map){
            return ((Map<String,String>)variables).get(name);
        }
        RequestPathMetadata definedPath = metadata.getDefinedPath();
        if (hasPlaceHolder(definedPath.getActualPath())) {
            RequestPathMetadata requestedPath = requestContext.getRequestPath();
            if (definedPath.match(requestedPath)) {
                Map<String, String> pathVariables = extractPathValues(definedPath.getActualPath(),
                        requestedPath.getActualPath());
                requestContext.getRequest().setAttribute(VALIABLES_CACHE_KEY, pathVariables);
                return pathVariables.get(name);
            }
        }
        // nothing path variables on this request.
        requestContext.getRequest().setAttribute(VALIABLES_CACHE_KEY,
                Maps.newHashMap(String.class, String.class));
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
