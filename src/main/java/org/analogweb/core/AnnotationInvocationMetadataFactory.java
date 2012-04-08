package org.analogweb.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.RequestPathMetadata;
import org.analogweb.annotation.Delete;
import org.analogweb.annotation.Get;
import org.analogweb.annotation.On;
import org.analogweb.annotation.Post;
import org.analogweb.annotation.Put;
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
    public InvocationMetadata createInvocationMetadata(Class<?> actionsClass, Method actionMethod) {
        On typePathMapping = actionsClass.getAnnotation(On.class);
        On methodPathMapping = actionMethod.getAnnotation(On.class);
        if (typePathMapping != null && methodPathMapping != null) {
            return new DefaultInvocationMetadata(actionsClass, actionMethod.getName(),
                    actionMethod.getParameterTypes(), relativeRequestPath(actionsClass,
                            actionMethod, typePathMapping, methodPathMapping));
        } else {
            return null;
        }
    }

    protected RequestPathMetadata relativeRequestPath(Class<?> actionsClass, Method actionMethod,
            On typePathMapping, On methodPathMapping) {
        String editedRoot = typePathMapping.value();
        if (StringUtils.isEmpty(editedRoot)) {
            editedRoot = actionsClass.getSimpleName().replace("Actions", "").toLowerCase();
        }
        String editedPath = methodPathMapping.value();
        if (StringUtils.isEmpty(editedPath)) {
            editedPath = actionMethod.getName();
        }
        return newRequestPathDefinition(editedRoot, editedPath, resolveRequestMethods(actionMethod));
    }

    protected RequestPathMetadata newRequestPathDefinition(String root, String path,
            String[] methods) {
        return RequestPathDefinition.define(root, path, methods);
    }

    private String[] resolveRequestMethods(Method actionMethod) {
        List<String> methods = new ArrayList<String>();
        if (actionMethod.getAnnotation(Post.class) != null) {
            methods.add("POST");
        }
        if (actionMethod.getAnnotation(Get.class) != null) {
            methods.add("GET");
        }
        if (actionMethod.getAnnotation(Delete.class) != null) {
            methods.add("DELETE");
        }
        if (actionMethod.getAnnotation(Put.class) != null) {
            methods.add("PUT");
        }
        if (methods.isEmpty()) {
            methods = Arrays.asList("GET", "POST");
        }
        return methods.toArray(new String[methods.size()]);
    }

}
