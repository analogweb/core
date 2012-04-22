package org.analogweb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return getInstanceQuietly(clazz, new Object[0]);
    }

    public static <T> T getInstanceQuietly(Class<T> clazz, Object... args) {
        List<Class<?>> argTypes = new ArrayList<Class<?>>();
        for (Object arg : args) {
            argTypes.add(arg.getClass());
        }
        try {
            final Constructor<T> constructor = clazz.getConstructor(argTypes
                    .toArray(new Class<?>[argTypes.size()]));
            AccessController.doPrivileged(new PrivilegedAction<T>() {
                @Override
                public T run() {
                    constructor.setAccessible(true);
                    return null;
                }
            });
            return constructor.newInstance(args);
        } catch (SecurityException e) {
            log.log("TU000008", e, clazz);
        } catch (NoSuchMethodException e) {
            log.log("TU000008", e, clazz);
        } catch (IllegalArgumentException e) {
            log.log("TU000008", e, clazz);
        } catch (InstantiationException e) {
            log.log("TU000008", e, clazz);
        } catch (IllegalAccessException e) {
            log.log("TU000008", e, clazz);
        } catch (InvocationTargetException e) {
            log.log("TU000008", e, clazz);
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
    
    public static Method[] getMethods(Class<?> clazz){
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
            log.log("DU000009", e, field);
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
            log.log("DU000009", e, field);
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
                    log.log("DU000010", e, instance, fieldName);
                } catch (NoSuchFieldException e) {
                    log.log("DU000010", e, instance, fieldName);
                } catch (IllegalArgumentException e) {
                    log.log("DU000010", e, instance, fieldName);
                } catch (IllegalAccessException e) {
                    log.log("DU000010", e, instance, fieldName);
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
            log.log("DU000011", e, clazz, methodName);
        } catch (NoSuchMethodException e) {
            log.log("DU000011", e, clazz, methodName);
        }
    	// try declared method.
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            log.log("DU000011", e, clazz, methodName);
        } catch (NoSuchMethodException e) {
            log.log("DU000011", e, clazz, methodName);
        }
        return null;
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

}
