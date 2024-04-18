package wtf.casper.amethyst.core.inject;

import wtf.casper.amethyst.core.utils.Lazy;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class Inject {

    private static Map<InjectionContainer, Map<Class<?>, Object>> containerInstances = new HashMap<>();
    private static Map<InjectionContainer, Map<Class<?>, Supplier<?>>> containerSuppliers = new HashMap<>();


    public static <T> void bind(Class<T> clazz, T instance, @Nullable InjectionContainer container) {
        if (container == null) container = InjectionContainer.GLOBAL;

        Map<Class<?>, Object> map = containerInstances.computeIfAbsent(container, c -> new HashMap<>());
        if (map.containsKey(clazz)) {
            throw new IllegalStateException("Instance already bound for " + clazz.getName());
        }
        map.put(clazz, instance);
    }

    public static <T> void bind(Class<T> clazz, Supplier<T> supplier, @Nullable InjectionContainer container) {
        if (container == null) container = InjectionContainer.GLOBAL;

        Map<Class<?>, Supplier<?>> map = containerSuppliers.computeIfAbsent(container, c -> new HashMap<>());
        if (map.containsKey(clazz)) {
            throw new IllegalStateException("Supplier already bound for " + clazz.getName());
        }
        map.put(clazz, supplier);
    }

    public static <T> void bind(Class<T> clazz, Supplier<T> supplier) {
        bind(clazz, supplier, null);
    }

    public static <T> void bind(Class<T> clazz, T instance) {
        bind(clazz, instance, null);
    }

    public static <T> T get(Class<T> clazz) {
        return get(clazz, null);
    }

    public static <T> T get(Class<T> clazz, InjectionContainer container) {
        if (container == null) container = InjectionContainer.GLOBAL;

        T t = getNullable(clazz, container);
        if (t == null) {
            throw new IllegalStateException("No instance or provider found for " + clazz.getName());
        }
        return t;
    }

    public static <T> Lazy<T> getLater(Class<T> clazz) {
        return getLater(clazz, null);
    }

    public static <T> Lazy<T> getLater(Class<T> clazz, InjectionContainer container) {
        if (container == null) container = InjectionContainer.GLOBAL;

        InjectionContainer finalContainer = container;
        return new Lazy<>(() -> {
            return get(clazz, finalContainer);
        });
    }

    private static <T> T getNullable(Class<T> clazz, InjectionContainer container) {
        if (container == null) container = InjectionContainer.GLOBAL;

        final Set<Class<?>> scanned = new HashSet<>();
        final List<Class<?>> toScan = new ArrayList<>();
        toScan.add(clazz);

        while (!toScan.isEmpty()) {
            final Class<?> current = toScan.remove(0);
            if (scanned.contains(current)) continue;
            scanned.add(current);

            // scan current container for instance or supplier
            final Map<Class<?>, Object> instances = containerInstances.get(container);
            if (instances != null) {
                final Object instance = instances.get(current);
                if (instance != null) {
                    //noinspection unchecked
                    return (T) instance;
                }
            }

            final Map<Class<?>, Supplier<?>> suppliers = containerSuppliers.get(container);
            if (suppliers != null) {
                final Supplier<?> supplier = suppliers.get(current);
                if (supplier != null) {
                    //noinspection unchecked
                    return (T) supplier.get();
                }
            }

            // add the super class and interfaces to scan next
            final Class<?> superclass = current.getSuperclass();
            if (superclass != null) {
                toScan.add(superclass);
            }

            toScan.addAll(Arrays.asList(current.getInterfaces()));
        }

        return null;
    }
}
