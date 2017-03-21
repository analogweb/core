package org.analogweb.core;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import org.analogweb.ContainerAdaptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.RequestPathMetadata;
import org.analogweb.annotation.HttpMethod;
import org.analogweb.annotation.Route;
import org.analogweb.util.*;

/**
 * @author snowgoose
 */
public class AnnotationInvocationMetadataFactory implements InvocationMetadataFactory {

    @Override
    public Collection<InvocationMetadata> createInvocationMetadatas(ContainerAdaptor instanceProvider) {
        Collection<Class<?>> invocationClasses = collectAllClasses();
        List<InvocationMetadata> metadatas = new ArrayList<InvocationMetadata>();
        for(Class<?> invocationClass : invocationClasses) {
            Method[] methods = ReflectionUtils.getMethods(invocationClass);
            for (Method method : methods) {
                InvocationMetadata metadata = createInvocationMetadata(invocationClass, method);
                if (metadata != null) {
                    metadatas.add(metadata);
                }
            }
        }
        return metadatas;
    }

    InvocationMetadata createInvocationMetadata(Class<?> invocationClass, Method invocationMethod) {
        Route typePathMapping = AnnotationUtils.findAnnotation(Route.class, invocationClass);
        Route methodPathMapping = invocationMethod.getAnnotation(Route.class);
        if (typePathMapping != null && methodPathMapping != null) {
            return new DefaultInvocationMetadata(invocationClass, invocationMethod.getName(),
                    invocationMethod.getParameterTypes(), relativeRequestPath(invocationClass,
                            invocationMethod, typePathMapping, methodPathMapping));
        } else {
            return null;
        }
    }

    protected RequestPathMetadata relativeRequestPath(Class<?> invocationClass,
            Method invocationMethod, Route typePathMapping, Route methodPathMapping) {
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
    protected Collection<Class<?>> collectAllClasses() {
        Collection<Class<?>> collectedClasses = new HashSet<Class<?>>();
        for (String resourceName : SystemProperties.classPathes()) {
            URL resourceURL = ResourceUtils.findResource(resourceName);
            for (ClassCollector collector : getClassCollectors()) {
                collectedClasses.addAll(collector.collect(StringUtils.EMPTY, resourceURL,
                        Thread.currentThread()
                                .getContextClassLoader()));
            }
        }
        return collectedClasses;
    }

    protected List<ClassCollector> getClassCollectors() {
        List<ClassCollector> list = new ArrayList<ClassCollector>();
        list.add(new JarClassCollector());
        list.add(new FileClassCollector());
        return Collections.unmodifiableList(list);
    }
}
