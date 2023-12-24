package org.analogweb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for {@link Annotation}
 *
 * @author snowgoose
 */
public final class AnnotationUtils {

    public static <T extends Annotation> T findAnnotation(Class<T> target, Class<?> annotationContainsClass) {
        Assertion.notNull(annotationContainsClass, "Class must not be null");
        if (Annotation.class.isAssignableFrom(annotationContainsClass)
                && !annotationContainsClass.getPackage().equals(Annotation.class.getPackage())) {
            return findAnnotation(target, annotationContainsClass.getAnnotations());
        }
        T annotation = annotationContainsClass.getAnnotation(target);
        if (annotation != null) {
            return annotation;
        }
        for (Class<?> ifc : annotationContainsClass.getInterfaces()) {
            annotation = findAnnotation(target, ifc);
            if (annotation != null) {
                return annotation;
            }
        }
        if (!Annotation.class.isAssignableFrom(annotationContainsClass)) {
            for (Annotation ann : annotationContainsClass.getAnnotations()) {
                annotation = findAnnotation(target, ann.annotationType());
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        Class<?> superClass = annotationContainsClass.getSuperclass();
        if (superClass == null || superClass == Object.class) {
            return null;
        }
        return findAnnotation(target, superClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T findAnnotation(Class<T> target, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            Class<?> annotationType = annotation.annotationType();
            if (annotationType.equals(target) || (annotation = findAnnotation(target, annotationType)) != null) {
                return (T) annotation;
            }
        }
        return null;
    }

    public static <T extends Annotation> List<T> findAnnotations(Class<T> target, Method method) {
        List<T> annotations = new ArrayList<T>();
        for (Annotation ann : method.getAnnotations()) {
            T an = findAnnotation(target, ann);
            if (an != null) {
                annotations.add(an);
            }
        }
        return annotations;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Annotation annotation) {
        return (T) getValue(annotation, "value");
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Annotation annotation, String attributeName) {
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
            return (T) method.invoke(annotation);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isDeclared(Class<? extends Annotation> expectDeclared, Class<?> clazz) {
        for (Annotation an : clazz.getDeclaredAnnotations()) {
            if (an.annotationType().equals(expectDeclared)) {
                return true;
            }
        }
        return false;
    }
}
