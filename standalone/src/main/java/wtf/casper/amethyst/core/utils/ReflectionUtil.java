package wtf.casper.amethyst.core.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtil {

    @SneakyThrows
    public static void setPrivateField(Object object, String field, Object newValue) {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        objectField.set(object, newValue);
        objectField.setAccessible(false);
    }

    @SneakyThrows
    public static void setPrivateField(Class<?> clazz, String field, Object newValue) {
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        objectField.set(null, newValue);
        objectField.setAccessible(false);
    }

    @SneakyThrows
    public static Object getPrivateField(Object object, String field) {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object val = objectField.get(object);
        objectField.setAccessible(false);
        return val;
    }

    @SneakyThrows
    public static Object getPrivateField(Class<?> clazz, String field) {
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object val = objectField.get(null);
        objectField.setAccessible(false);
        return val;
    }

    /**
     * Finds a declared field in given class.
     */
    public static Field findDeclaredField(Class<?> clazz, Class<?> type, String name) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                return field;
            }
        }

        throw new IllegalStateException("Can't find field " + clazz.getName() + "#" + name);
    }

    public static Collection<Class<?>> getClassesWithAnnotation(String packagePath, Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz : getClasses(packagePath, Object.class)) {
            if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    public static Collection<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return getClassesWithAnnotation("wtf.casper", annotation);
    }

    public static <T> Class<T>[] getClasses(Class<T> clazz) {
        return getClasses("wtf.casper", clazz);
    }

    public static <T> Class<T>[] getClasses(String packagePath, Class<T> clazz) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(clazz);
        return subTypesOf.toArray(new Class[0]);
    }

    /**
     * Finds a declared method in given class.
     */
    public static Method findDeclaredMethod(Class<?> clazz, Class<?>[] paramTypes, Class<?> returnType, String name) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.getReturnType().equals(returnType)) continue;
            if (!Arrays.equals(paramTypes, method.getParameterTypes())) continue;

            method.setAccessible(true);
            return method;
        }

        throw new IllegalStateException("Can't find method " + clazz.getName() + "." + name);
    }

    /**
     * Retrieves all methods with given annotation.
     */
    public static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> methods = new ArrayList<>(declaredMethods.length);
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static <V> Field[] getAllFields(Class<V> valueClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = valueClass;
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static <V> V createInstance(Class<V> valueClass) {
        return createInstance(valueClass, new Class<?>[0], new Object[0]);
    }

    public static <V> V createInstance(Class<V> valueClass, Class<?>[] paramTypes, Object[] params) {
        try {
            return valueClass.getConstructor(paramTypes).newInstance(params);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V createInstance(Class<V> valueClass, Object... params) {
        Class<?>[] paramTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }
        return createInstance(valueClass, paramTypes, params);
    }

    /**
     * Get the generic type of class (e.g. List<String> -> String)
     * @param clazz the class to get the generic type from
     * @param index the index of the generic type
     * @return the generic type
     * */
    @Nullable
    public static <T> Class<T> getGenericType(Class<T> clazz, int index) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            try {
                return (Class<T>) actualTypeArguments[index];
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * @param v         the object to get the field from
     * @param fieldName the name of the field
     * @return an optional containing the value of the field or empty if the field does not exist
     */
    public static <V> Optional<Object> getFieldValue(V v, String fieldName) {
        try {
            Field field = v.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return Optional.ofNullable(field.get(v));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * @param v         the object to get the field from
     * @param fieldName the name of the field
     * @return an optional containing the value of the field or empty if the field does not exist
     */
    public static <V, T> Optional<T> getFieldValue(V v, String fieldName, Class<T> returnType) {
        try {
            Field field = v.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Optional<Object> optional = Optional.ofNullable(field.get(v));
            return optional.map(returnType::cast);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            return Optional.empty();
        }
    }
}
