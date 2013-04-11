package org.analogweb.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.RequestPathMetadata;
import org.analogweb.annotation.HttpMethod;
import org.analogweb.annotation.On;
import org.analogweb.util.AnnotationUtils;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class AnnotationInvocationMetadataFactory implements InvocationMetadataFactory {

    @Override
    public boolean containsInvocationClass(Class<?> clazz) {
        return clazz.getAnnotation(On.class) != null;
    }

    @Override
    public InvocationMetadata createInvocationMetadata(Class<?> invocationClass,
            Method invocationMethod) {
        On typePathMapping = AnnotationUtils.findAnnotation(On.class, invocationClass);
        On methodPathMapping = invocationMethod.getAnnotation(On.class);
        if (typePathMapping != null && methodPathMapping != null) {
            return new DefaultInvocationMetadata(invocationClass, invocationMethod.getName(),
                    invocationMethod.getParameterTypes(), relativeRequestPath(invocationClass,
                            invocationMethod, typePathMapping, methodPathMapping));
        } else {
            return null;
        }
    }

    protected RequestPathMetadata relativeRequestPath(Class<?> invocationClass,
            Method invocationMethod, On typePathMapping, On methodPathMapping) {
        String editedRoot = typePathMapping.value();
        if (StringUtils.isEmpty(editedRoot)) {
            editedRoot = invocationClass.getSimpleName().replace("Resource", "").toLowerCase();
        }
        String editedPath = methodPathMapping.value();
        if (StringUtils.isEmpty(editedPath)) {
            editedPath = invocationMethod.getName();
        }
        return newRequestPathDefinition(editedRoot, editedPath,
                resolveRequestMethods(invocationMethod));
    }

    protected RequestPathMetadata newRequestPathDefinition(String root, String path,
            String[] methods) {
        return RequestPathDefinition.define(root, path, methods);
    }

    private String[] resolveRequestMethods(Method method) {
        List<String> methods;
        List<HttpMethod> httpMethods = AnnotationUtils.findAnnotations(HttpMethod.class, method);
        if (CollectionUtils.isEmpty(httpMethods)) {
            methods = Arrays.asList("GET", "POST");
            return methods.toArray(new String[methods.size()]);
        }
        methods = new ArrayList<String>();
        for (HttpMethod hm : httpMethods) {
            methods.add(hm.value());
        }
        return methods.toArray(new String[methods.size()]);
    }
}
