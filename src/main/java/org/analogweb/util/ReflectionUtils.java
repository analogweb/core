package org.analogweb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.analogweb.InvocationMetadata;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public final class ReflectionUtils {

    private static final Log log = Logs.getLog(ReflectionUtils.class);

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getMethodParameterAnnotation(Method method,
            Class<T> annotationClass, int parameterIndex) {
        Annotation[][] annotations = method.getParameterAnnotations();
        if (annotations.length > parameterIndex) {
            for (Annotation annotation : annotations[parameterIndex]) {
                if (annotation.annotationType().equals(annotationClass)) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }

    public static <T> T getInstanceQuietly(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            log.log("TU000008", e, new Object[] { clazz });
        } catch (IllegalAccessException e) {
            log.log("TU000008", e, new Object[] { clazz });
        }
        return null;
    }

    public static Object getInstanceQuietly(Constructor<?> constructor, Object... args) {
        return getInstanceQuietly(Object.class, constructor, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstanceQuietly(Class<T> type, Constructor<?> constructor,
            Object... args) {
        try {
            if (constructor == null) {
                return null;
            }
            return (T) constructor.newInstance(args);
        } catch (IllegalArgumentException e) {
            log.log("TU000008", e, new Object[] { constructor.getDeclaringClass() });
        } catch (InstantiationException e) {
            log.log("TU000008", e, new Object[] { constructor.getDeclaringClass() });
        } catch (IllegalAccessException e) {
            log.log("TU000008", e, new Object[] { constructor.getDeclaringClass() });
        } catch (InvocationTargetException e) {
            log.log("TU000008", e, new Object[] { constructor.getDeclaringClass() });
        }
        return null;
    }

    public static <T> List<Class<T>> filterClassAsImplementsInterface(Class<T> filteringType,
            Collection<Class<?>> collectedClasses) {
        return filterClassAsImplementsInterface(filteringType, collectedClasses,
                ASSIGNABLE_FROM_FILTER);
    }

    public static <T> List<Class<T>> filterClassAsImplementsInterface(Class<T> filteringType,
            Collection<Class<?>> collectedClasses, TypeFilter typeFilter) {
        List<Class<T>> implementsTypes = new ArrayList<Class<T>>();
        for (Class<?> clazz : collectedClasses) {
            Class<T> filterResult = typeFilter.filterType(clazz, filteringType);
            if (filterResult != null) {
                implementsTypes.add(filterResult);
            }
        }
        return implementsTypes;
    }

    public static Method[] getMethods(Class<?> clazz) {
        return clazz.getMethods();
    }

    public interface TypeFilter {

        <T> Class<T> filterType(Class<?> actualClass, Class<T> filteringType);
    }

    private static final TypeFilter ASSIGNABLE_FROM_FILTER = new TypeFilter() {

        @Override
        @SuppressWarnings("unchecked")
        public <T> Class<T> filterType(Class<?> actualClass, Class<T> filteringType) {
            if (filteringType.isAssignableFrom(actualClass)) {
                return (Class<T>) actualClass;
            } else {
                return null;
            }
        }
    };

    public static void writeValueToField(Field field, Object instance, Object value) {
        if (field == null) {
            return;
        }
        try {
            setAccessible(field);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            // swallow
            log.log("TU000009", e, field);
        }
    }

    public static void writeValueToField(String fieldName, Object instance, Object value) {
        Field field = getAccessibleField(instance.getClass(), fieldName);
        if (field == null) {
            return;
        }
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            // swallow
            log.log("TU000009", e, field);
        }
    }

    public static Field getAccessibleField(final Class<?> targetClass, final String fieldName) {
        Field field;
        try {
            field = targetClass.getDeclaredField(fieldName);
            if (Modifier.isPublic(field.getModifiers()) == false) {
                setAccessible(field);
            }
            return field;
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static void setAccessible(final Field field) {
        AccessController.doPrivileged(new PrivilegedAction<Field>() {

            @Override
            public Field run() {
                try {
                    if (Modifier.isPublic(field.getModifiers()) == false && !field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    return field;
                } catch (SecurityException e) {
                    // ignore.
                    return null;
                }
            }
        });
    }

    public static Object getValueOfField(final String fieldName, final int modifier,
            final Object instance) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {

            @Override
            public Object run() {
                try {
                    Field field = instance.getClass().getDeclaredField(fieldName);
                    if (Modifier.isPrivate(modifier)) {
                        field.setAccessible(true);
                    }
                    return field.get(instance);
                } catch (SecurityException e) {
                    log.log("TU000010", e, instance, fieldName);
                } catch (NoSuchFieldException e) {
                    log.log("TU000010", e, instance, fieldName);
                } catch (IllegalArgumentException e) {
                    log.log("TU000010", e, instance, fieldName);
                } catch (IllegalAccessException e) {
                    log.log("TU000010", e, instance, fieldName);
                }
                return null;
            }
        });
    }

    public static Method getMethodQuietly(Class<?> clazz, String methodName,
            Class<?>[] parameterTypes) {
        // try inherit method.
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            log.log("TU000011", e, clazz, methodName);
        } catch (NoSuchMethodException e) {
            log.log("TU000011", e, clazz, methodName);
        }
        // try declared method.
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            log.log("TU000011", e, clazz, methodName);
        } catch (NoSuchMethodException e) {
            log.log("TU000011", e, clazz, methodName);
        }
        return null;
    }

    public static Method getInvocationMethodDefault(InvocationMetadata metadata) {
        Method method = getMethodQuietly(metadata.getInvocationClass(), metadata.getMethodName(),
                metadata.getArgumentTypes());
        if (method == null) {
            return getMethodQuietly(ReflectionUtils.class, "nop", new Class<?>[0]);
        }
        return method;
    }

    public void nop() {
        // nop.
    }

    public static Method getInvocationMethod(InvocationMetadata metadata) {
        return getMethodQuietly(metadata.getInvocationClass(), metadata.getMethodName(),
                metadata.getArgumentTypes());
    }

    public static Set<Class<?>> findAllImplementsInterfacesRecursivery(Class<?> find) {
        Set<Class<?>> result = new HashSet<Class<?>>();
        return findAllImplementsInterfacesRecursivery(find, result);
    }

    private static Set<Class<?>> findAllImplementsInterfacesRecursivery(Class<?> find,
            Set<Class<?>> result) {
        if (find == null) {
            return result;
        }
        Class<?> supreClass = find.getSuperclass();
        Class<?>[] interfaces;
        if (supreClass != null && supreClass.equals(Object.class) == false) {
            interfaces = supreClass.getInterfaces();
        } else {
            interfaces = find.getInterfaces();
        }
        for (Class<?> interfaci : interfaces) {
            result.add(interfaci);
            findAllImplementsInterfacesRecursivery(interfaci, result);
        }
        return result;
    }

    public static ParameterizedType findParameterizedType(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }
        final Type genericType = clazz.getGenericSuperclass();
        if (genericType instanceof ParameterizedType) {
            return (ParameterizedType) genericType;
        } else {
            final Type[] genericInterfaceTypes = clazz.getGenericInterfaces();
            for (final Type genericInterfaceType : genericInterfaceTypes) {
                if (genericInterfaceType instanceof ParameterizedType) {
                    return (ParameterizedType) genericInterfaceType;
                }
            }
            final Class<?> next = (Class<?>) clazz.getGenericSuperclass();
            return findParameterizedType(next);
        }
    }

    public static List<Class<?>> getCallerClasses() {
        return getCallerClasses(Thread.currentThread());
    }

    public static List<Class<?>> getCallerClasses(Thread thread) {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        for (StackTraceElement ste : thread.getStackTrace()) {
            String className = ste.getClassName();
            if (className.equals(ReflectionUtils.class.getName()) == false
                    && className.contains("java.lang.Thread") == false) {
                classes.add(ClassUtils.forNameQuietly(className));
            }
        }
        return classes;
    }

}
