package wtf.casper.amethyst.core.storage.id.utils;

import lombok.SneakyThrows;
import wtf.casper.amethyst.core.storage.id.Id;
import wtf.casper.amethyst.core.storage.id.exceptions.IdNotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class IdUtils {

    @SneakyThrows
    public static Object getId(final Class<?> type, final Object instance) {

        for (final Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Id.class)) {
                continue;
            }

            return field.get(instance);
        }

        final Method method = IdUtils.getIdMethod(type);

        if (method != null) {
            return method.invoke(instance);
        }

        throw new IdNotFoundException();
    }

    @SneakyThrows
    public static String getIdName(final Class<?> type) {

        for (final Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Id.class)) {
                continue;
            }

            return field.getName();
        }

        final Method method = IdUtils.getIdMethod(type);

        if (method != null) {
            return method.getName();
        }

        throw new IdNotFoundException();
    }

    @SneakyThrows
    public static Class<?> getIdClass(final Class<?> type) {

        for (final Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Id.class)) {
                continue;
            }

            return field.getDeclaringClass();
        }

        final Method method = IdUtils.getIdMethod(type);

        if (method != null) {
            return method.getDeclaringClass();
        }

        throw new IdNotFoundException();

    }

    private static Method getIdMethod(final Class<?> type) {

        for (final Method method : type.getDeclaredMethods()) {
            method.setAccessible(true);

            if (!method.isAnnotationPresent(Id.class)) {
                continue;
            }

            return method;
        }

        return null;
    }

    public static Field getIdField(final Class<?> type) {

        for (final Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Id.class)) {
                continue;
            }

            return field;
        }

        return null;
    }


}
