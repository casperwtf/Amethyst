package wtf.casper.amethyst.core.storage;

import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.obj.Pair;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface StatelessFieldStorage<K, V> {

    /**
     * @param field the field to search for.
     * @param value the value to search for.
     * @return a future that will complete with a collection of all values that match the given field and value.
     */
    default CompletableFuture<Collection<V>> get(final String field, final Object value) {
        return get(field, value, FilterType.EQUALS, SortingType.ASCENDING);
    }

    /**
     * @param field       the field to search for.
     * @param value       the value to search for.
     * @param filterType  the filter type to use.
     * @param sortingType the sorting type to use.
     * @return a future that will complete with a collection of all values that match the given field and value.
     */
    CompletableFuture<Collection<V>> get(final String field, final Object value, FilterType filterType, SortingType sortingType);

    /**
     * @param fields the fields to search for. Must contain all keys and values.
     * @return a future that will complete with a collection of all values that match the given fields and values.
     */
    default CompletableFuture<Collection<V>> get(Pair<String, Object>... fields) {
        return get(FilterType.EQUALS, SortingType.ASCENDING, fields);
    }

    /**
     * @param filterType  the filter type to use.
     * @param sortingType the sorting type to use.
     * @param fields      the fields to search for. Must contain all keys and values.
     * @return a future that will complete with a collection of all values that match the given fields and values.
     */
    default CompletableFuture<Collection<V>> get(FilterType filterType, SortingType sortingType, Pair<String, Object>... fields) {
        return CompletableFuture.supplyAsync(() -> {
            Collection<V> values = new ArrayList<>();
            get(fields[0].getFirst(), fields[0].getSecond(), filterType, sortingType).thenAccept(values::addAll).join();
            if (values.isEmpty()) {
                return values;
            }

            for (int i = 1; i < fields.length; i++) {
                final int index = i;
                values.removeIf((v) -> {
                    Optional<Object> optional = ReflectionUtil.getFieldValue(v, fields[index].getFirst());
                    return optional.isEmpty() || !optional.get().equals(fields[index].getSecond());
                });
            }

            return values;
        });
    }

    /**
     * @param key the key to search for.
     * @return a future that will complete with the value that matches the given key.
     */
    CompletableFuture<V> get(final K key);

    /**
     * @param key the key to search for.
     * @return a future that will complete with the value that matches the given key or a generated value if not found.
     */
    default CompletableFuture<V> getOrDefault(final K key) {
        return get(key).thenApply((v) -> {

            if (v != null) {
                return v;
            }

            if (getClass().isAssignableFrom(ConstructableValue.class)) {
                v = ((ConstructableValue<K, V>) this).constructValue(key);
                if (v == null) {
                    throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key
                            + ". Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.");
                }
                return v;
            }

            Class<V> aClass = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            try {
                return ReflectionUtil.createInstance(aClass, key);
            } catch (final Exception e) {
                throw new RuntimeException("Failed to create default value for " + aClass.getSimpleName() + " with key " + key + ". " +
                        "Please create a constructor in " + aClass.getSimpleName() + " for only the key.", e);
            }
        });
    }

    /**
     * @param field the field to search for.
     * @param value the value to search for.
     * @return a future that will complete with the first value that matches the given field and value.
     */
    default CompletableFuture<V> getFirst(final String field, final Object value) {
        return getFirst(field, value, FilterType.EQUALS);
    }

    /**
     * @param field      the field to search for.
     * @param value      the value to search for.
     * @param filterType the filter type to use.
     * @return a future that will complete with the first value that matches the given field and value.
     */
    CompletableFuture<V> getFirst(final String field, final Object value, FilterType filterType);


    /**
     * @param value the value to save.
     */
    CompletableFuture<Void> save(final V value);

    /**
     * @param values the values to save.
     */
    default CompletableFuture<Void> saveAll(final Collection<V> values) {
        return CompletableFuture.runAsync(() -> {
            values.forEach(this::save);
        });
    }

    /**
     * @param key the key to remove.
     */
    CompletableFuture<Void> remove(final V key);

    /**
     * Writes the storage to disk.
     */
    CompletableFuture<Void> write();

    /**
     * Closes the storage/storage connection.
     */
    default CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(() -> {

        });
    }

    /**
     * @param field the field to search for.
     * @param value the value to search for.
     * @return a future that will complete with a boolean that represents whether the storage contains a value that matches the given field and value.
     */
    default CompletableFuture<Boolean> contains(final String field, final Object value) {
        return CompletableFuture.supplyAsync(() -> getFirst(field, value).join() != null);
    }

    ;

    /**
     * @return a future that will complete with a collection of all values in the storage.
     */
    CompletableFuture<Collection<V>> allValues();

    enum SortingType {
        ASCENDING(String.class, Number.class, Boolean.class),
        DESCENDING(String.class, Number.class, Boolean.class);

        private final Class<?>[] types;

        SortingType(Class<?>... types) {
            this.types = types;
        }

        public boolean isApplicable(@NotNull final Class<?> type) {
            for (final Class<?> clazz : types) {
                if (clazz.isAssignableFrom(type)) {
                    return true;
                }
            }
            return false;
        }

        public Class<?>[] getTypes() {
            return types;
        }

        public <V> Collection<V> sort(Collection<V> values, String field) {
            if (values.isEmpty()) {
                return values;
            }
            V next = values.iterator().next();
            Optional<Object> fieldValue = ReflectionUtil.getFieldValue(next, field);
            if (fieldValue.isEmpty()) {
                throw new IllegalArgumentException("Field " + field + " does not exist in " + next.getClass().getSimpleName());
            }

            final Object o = fieldValue.get();
            if (!isApplicable(o.getClass())) {
                throw new IllegalArgumentException("Field " + field + " is not of a valid type for sorting.");
            }

            final List<V> list = new ArrayList<>(values);
            list.sort((o1, o2) -> {
                final Optional<Object> optional1 = ReflectionUtil.getFieldValue(o1, field);
                final Optional<Object> optional2 = ReflectionUtil.getFieldValue(o2, field);
                if (optional1.isEmpty() || optional2.isEmpty()) {
                    return 0;
                }
                final Object o4 = optional1.get();
                final Object o5 = optional2.get();
                if (o instanceof String) {
                    return ((String) o4).compareTo((String) o5);
                }
                if (o instanceof Number) {
                    return ((Number) o4).doubleValue() > ((Number) o5).doubleValue() ? 1 : -1;
                }
                if (o instanceof Boolean) {
                    return ((Boolean) o4).compareTo((Boolean) o5);
                }
                return 0;
            });
            if (this == DESCENDING) {
                Collections.reverse(list);
            }
            return list;
        }
    }

    enum FilterType {
        EQUALS(Object.class),
        CONTAINS(String.class),
        STARTS_WITH(String.class),
        ENDS_WITH(String.class),
        GREATER_THAN(Number.class),
        LESS_THAN(Number.class),
        GREATER_THAN_OR_EQUAL_TO(Number.class),
        LESS_THAN_OR_EQUAL_TO(Number.class),
        IN(Collection.class),
        NOT_EQUALS(Object.class),
        NOT_CONTAINS(String.class),
        NOT_STARTS_WITH(String.class),
        NOT_ENDS_WITH(String.class),
        NOT_GREATER_THAN(Number.class),
        NOT_LESS_THAN(Number.class),
        NOT_GREATER_THAN_OR_EQUAL_TO(Number.class),
        NOT_LESS_THAN_OR_EQUAL_TO(Number.class),
        NOT_IN(Collection.class);

        private final Class<?>[] types;

        FilterType(Class<?>... types) {
            this.types = types;
        }

        public boolean isApplicable(@NotNull final Class<?> type) {
            for (final Class<?> clazz : types) {
                if (clazz.isAssignableFrom(type)) {
                    return true;
                }
            }
            return false;
        }

        public Class<?>[] getTypes() {
            return types;
        }

        public boolean passes(Object object, Object value) {
            if (!isApplicable(object.getClass())) {
                return false;
            }

            switch (this) {
                case EQUALS -> {
                    return object.equals(value);
                }
                case CONTAINS -> {
                    return object.toString().contains(value.toString());
                }
                case STARTS_WITH -> {
                    return object.toString().startsWith(value.toString());
                }
                case ENDS_WITH -> {
                    return object.toString().endsWith(value.toString());
                }
                case GREATER_THAN -> {
                    return ((Number) object).doubleValue() > ((Number) value).doubleValue();
                }
                case LESS_THAN -> {
                    return ((Number) object).doubleValue() < ((Number) value).doubleValue();
                }
                case GREATER_THAN_OR_EQUAL_TO -> {
                    return ((Number) object).doubleValue() >= ((Number) value).doubleValue();
                }
                case LESS_THAN_OR_EQUAL_TO -> {
                    return ((Number) object).doubleValue() <= ((Number) value).doubleValue();
                }
                case IN -> {
                    if (!(value instanceof Collection)) {
                        return false;
                    }
                    return ((Collection<?>) value).contains(object);
                }
                case NOT_EQUALS -> {
                    return !object.equals(value);
                }
                case NOT_CONTAINS -> {
                    return !object.toString().contains(value.toString());
                }
                case NOT_STARTS_WITH -> {
                    return !object.toString().startsWith(value.toString());
                }
                case NOT_ENDS_WITH -> {
                    return !object.toString().endsWith(value.toString());
                }
                case NOT_GREATER_THAN -> {
                    return !(((Number) object).doubleValue() > ((Number) value).doubleValue());
                }
                case NOT_LESS_THAN -> {
                    return !(((Number) object).doubleValue() < ((Number) value).doubleValue());
                }
                case NOT_GREATER_THAN_OR_EQUAL_TO -> {
                    return !(((Number) object).doubleValue() >= ((Number) value).doubleValue());
                }
                case NOT_LESS_THAN_OR_EQUAL_TO -> {
                    return !(((Number) object).doubleValue() <= ((Number) value).doubleValue());
                }
                case NOT_IN -> {
                    if (!(value instanceof Collection)) {
                        return false;
                    }
                    return !(((Collection<?>) value).contains(object));
                }
                default -> {
                    return false;
                }
            }
        }
    }
}
