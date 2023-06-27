package wtf.casper.amethyst.core.storage;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.obj.Pair;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface StatelessFieldStorage<K, V> {

    /**
     * @param field the field to search for.
     * @param value the value to search for.
     * @return a future that will complete with a collection of all values that match the given field and value.
     */
    default CompletableFuture<Collection<V>> get(final String field, final Object value) {
        return get(field, value, FilterType.EQUALS, SortingType.NONE);
    }

    /**
     * @param field       the field to search for.
     * @param value       the value to search for.
     * @param filterType  the filter type to use.
     * @param sortingType the sorting type to use.
     * @return a future that will complete with a collection of all values that match the given field and value.
     */
    CompletableFuture<Collection<V>> get(final String field, final Object value, final FilterType filterType, final SortingType sortingType);

    /**
     * @param fields the fields to search for. Must contain all keys and values.
     * @return a future that will complete with a collection of all values that match the given fields and values.
     */
    default CompletableFuture<Collection<V>> get(Pair<String, Object>... fields) {
        return get(FilterType.EQUALS, SortingType.NONE, fields);
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
            if (fields == null || fields.length == 0) {
                return values;
            }
            get(fields[0].getFirst(), fields[0].getSecond(), filterType, sortingType).thenAccept(values::addAll).join();
            if (values.isEmpty()) {
                return values;
            }

            if (fields.length == 1) {
                return values;
            }

            for (int i = 1; i < fields.length; i++) {
                final int index = i;
                values.removeIf((v) -> {
                    String[] allFields = fields[index].getFirst().split("\\.");
                    if (allFields.length == 1) {
                        Optional<Object> optional = ReflectionUtil.getFieldValue(v, allFields[0]);
                        return optional.isEmpty() || filterType.passes(optional.get(), allFields[0], fields[index].getSecond());
                    }

                    Iterator<String> iterator = Arrays.stream(allFields).iterator();
                    Object object = v;
                    while (iterator.hasNext()) {
                        String field = iterator.next();
                        Optional<Object> optional = ReflectionUtil.getFieldValue(object, field);
                        if (optional.isEmpty()) {
                            return true;
                        }
                        object = optional.get();
                        if (!iterator.hasNext()) {
                            return filterType.passes(object, field, fields[index].getSecond());
                        }
                    }

                    return true;
                });
            }

            return values;
        });
    }

    /**
     * @param key the key to search for.
     * @return a future that will complete with the value that matches the given key.
     * The value may be null if the key is not found.
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

            if (this instanceof ConstructableValue<?, ?>) {
                v = ((ConstructableValue<K, V>) this).constructValue(key);
                if (v == null) {
                    throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key
                            + ". Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.");
                }
                return v;
            }

            if (this instanceof KeyValue<?,?>) {
                KeyValue<K, V> keyValueGetter = (KeyValue<K, V>) this;
                try {
                    return ReflectionUtil.createInstance(keyValueGetter.value(), key);
                } catch (final Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key + ". " +
                            "Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.", e);
                }
            }

            try {
                if (getClass().getGenericSuperclass() instanceof ParameterizedType parameterizedType) {
                    Type type = parameterizedType.getActualTypeArguments()[1];
                    System.out.println(type.getTypeName());
                    Class<V> aClass = (Class<V>) Class.forName(type.getTypeName());
                    return ReflectionUtil.createInstance(aClass, key);
                }

                throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key + ". " +
                        "Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.");

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key + ". " +
                        "Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.");
            }

//            try {
//                Type[] arguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
//                Class<V> aClass = (Class<V>) arguments[1];
//                return ReflectionUtil.createInstance(aClass, key);
//            } catch (final Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("Failed to create default value for " + v.getClass().getSimpleName() + " with key " + key + ". " +
//                        "Please create a constructor in " + v.getClass().getSimpleName() + " for only the key.", e);
//            }
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

    /**
     * @param storage the storage to migrate from. The data will be copied from the given storage to this storage.
     * @return a future that will complete with a boolean that represents whether the migration was successful.
     */
    default CompletableFuture<Boolean> migrate(final StatelessFieldStorage<K, V> storage) {
        return CompletableFuture.supplyAsync(() -> {
            storage.allValues().thenAccept((values) -> {
                values.forEach(this::save);
            }).join();
            return true;
        });
    }

    /**
     * @param oldStorageSupplier supplier to provide the old storage
     * @param config             the config
     * @param path               the path to the storage
     * @return a future that will complete with a boolean that represents whether the migration was successful.
     */
    default CompletableFuture<Boolean> migrateFrom(Supplier<StatelessFieldStorage<K, V>> oldStorageSupplier, YamlDocument config, String path) {
        return CompletableFuture.supplyAsync(() -> {
            if (config == null) return false;
            Section section = config.getSection(path);
            if (section == null) return false;
            if (!section.getBoolean("migrate", false)) return false;
            section.set("migrate", false);
            try {
                config.save();
            } catch (IOException e) {
                AmethystLogger.error("Failed to save config");
                e.printStackTrace();
            }
            // storage that we are migrating to the new storage
            StatelessFieldStorage<K, V> oldStorage = oldStorageSupplier.get();
            try {
                this.migrate(oldStorage).join();
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * @return a future that will complete with a collection of all values in the storage.
     */
    CompletableFuture<Collection<V>> allValues();

    /**
     * @param field       the field to search for.
     * @param sortingType the sorting type to use.
     * @return a future that will complete with a collection of all values in the storage that match the given field and value.
     */
    default CompletableFuture<Collection<V>> allValues(String field, SortingType sortingType) {
        return CompletableFuture.supplyAsync(() -> {
            Collection<V> values = allValues().join();
            if (values.isEmpty()) {
                return values;
            }

            // Sort the values.
            return sortingType.sort(values, field);
        });
    }


    enum SortingType {
        NONE(Object.class),
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
            if (values.isEmpty() || this == NONE) {
                return values;
            }

            values = new ArrayList<>(values);

            // Get the first value and check if the field exists.
            V next = values.iterator().next();
            Optional<Object> fieldValue = ReflectionUtil.getFieldValue(next, field);
            if (fieldValue.isEmpty()) {
                throw new IllegalArgumentException("Field " + field + " does not exist in " + next.getClass().getSimpleName());
            }

            // Check if the field is of a valid type for sorting.
            final Object o = fieldValue.get();
            if (!isApplicable(o.getClass()) && !(o instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("Field " + field + " is not of a valid type for sorting.");
            }

            // Sort the values if map
            if (o instanceof Map<?, ?>) {
                // check if map key and value are sortable
                Iterator<? extends Map.Entry<?, ?>> entryIterator = ((Map<?, ?>) o).entrySet().iterator();
                Map.Entry<?, ?> entry = entryIterator.next();
                if (!isApplicable(entry.getKey().getClass()) || !isApplicable(entry.getValue().getClass())) {
                    throw new IllegalArgumentException("Field " + field + " is not of a valid type for sorting.");
                }

                // we prefer to sort by value because the key is usually the id
                boolean useValue = isApplicable(entry.getValue().getClass());

                if (useValue) {
                    values = values.stream().sorted((o1, o2) -> {
                        Object o1Value = ((Map<?, ?>) ReflectionUtil.getFieldValue(o1, field).get()).values().iterator().next();
                        Object o2Value = ((Map<?, ?>) ReflectionUtil.getFieldValue(o2, field).get()).values().iterator().next();

                        if (o1Value instanceof Comparable) {
                            return ((Comparable) o1Value).compareTo(o2Value);
                        }

                        if (o1Value instanceof Number) {
                            return Double.compare(((Number) o1Value).doubleValue(), ((Number) o2Value).doubleValue());
                        }

                        return o1Value.toString().compareTo(o2Value.toString());
                    }).collect(Collectors.toList());
                } else {
                    values = values.stream().sorted((o1, o2) -> {
                        Object o1Value = ((Map<?, ?>) ReflectionUtil.getFieldValue(o1, field).get()).keySet().iterator().next();
                        Object o2Value = ((Map<?, ?>) ReflectionUtil.getFieldValue(o2, field).get()).keySet().iterator().next();

                        if (o1Value instanceof Comparable) {
                            return ((Comparable) o1Value).compareTo(o2Value);
                        }

                        if (o1Value instanceof Number) {
                            return Double.compare(((Number) o1Value).doubleValue(), ((Number) o2Value).doubleValue());
                        }

                        return o1Value.toString().compareTo(o2Value.toString());
                    }).collect(Collectors.toList());
                }

                if (this == DESCENDING) {
                    Collections.reverse((List<?>) values);
                }

                return values;
            }

            values = values.stream().sorted((o1, o2) -> {
                Object o1Value = ReflectionUtil.getFieldValue(o1, field).get();
                Object o2Value = ReflectionUtil.getFieldValue(o2, field).get();

                if (o1Value instanceof Comparable) {
                    return ((Comparable) o1Value).compareTo(o2Value);
                }

                if (o1Value instanceof Number) {
                    return Double.compare(((Number) o1Value).doubleValue(), ((Number) o2Value).doubleValue());
                }

                return o1Value.toString().compareTo(o2Value.toString());
            }).collect(Collectors.toList());

            if (this == DESCENDING) {
                Collections.reverse((List<?>) values);
            }

            return values;
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
        IN(Collection.class, Map.class),
        NOT_EQUALS(Object.class),
        NOT_CONTAINS(String.class),
        NOT_STARTS_WITH(String.class),
        NOT_ENDS_WITH(String.class),
        NOT_GREATER_THAN(Number.class),
        NOT_LESS_THAN(Number.class),
        NOT_GREATER_THAN_OR_EQUAL_TO(Number.class),
        NOT_LESS_THAN_OR_EQUAL_TO(Number.class),
        NOT_IN(Collection.class, Map.class);

        private final Class<?>[] types;

        FilterType(Class<?>... types) {
            this.types = types;
        }

        public boolean isApplicable(@NotNull final Class<?> type) {
            for (final Class<?> clazz : getTypes()) {
                if (clazz.isAssignableFrom(type)) {
                    return true;
                }
            }
            return false;
        }

        public Class<?>[] getTypes() {
            return types;
        }

        /**
         * @param object    the object we are checking.
         * @param fieldName the name of the field we are checking.
         * @param value     the value we are checking for.
         */
        public boolean passes(Object object, String fieldName, Object value) {
            if (value == null) {
                return false;
            }

            Optional<Object> o = ReflectionUtil.getFieldValue(object, fieldName);
            if (o.isEmpty()) {
                return false;
            }

            Object field = o.get();
            if (!isApplicable(field.getClass()) && !(field instanceof Collection) && !(field instanceof Map)) {
                return false;
            }

            switch (this) {
                case EQUALS -> {
                    return field.equals(value);
                }
                case CONTAINS -> {
                    return field.toString().contains(value.toString());
                }
                case STARTS_WITH -> {
                    return field.toString().startsWith(value.toString());
                }
                case ENDS_WITH -> {
                    return field.toString().endsWith(value.toString());
                }
                case GREATER_THAN -> {
                    return ((Number) field).doubleValue() > ((Number) value).doubleValue();
                }
                case LESS_THAN -> {
                    return ((Number) field).doubleValue() < ((Number) value).doubleValue();
                }
                case GREATER_THAN_OR_EQUAL_TO -> {
                    return ((Number) field).doubleValue() >= ((Number) value).doubleValue();
                }
                case LESS_THAN_OR_EQUAL_TO -> {
                    return ((Number) field).doubleValue() <= ((Number) value).doubleValue();
                }
                case IN -> {
                    if ((field instanceof Collection)) {
                        return ((Collection<?>) field).contains(value);
                    }
                    if (field.getClass().isArray()) {
                        return Arrays.asList(field).contains(value);
                    }
                    if (field instanceof Map<?, ?>) {
                        return ((Map<?, ?>) field).containsKey(value) || ((Map<?, ?>) field).containsValue(value);
                    }
                    return false;
                }
                case NOT_EQUALS -> {
                    return !field.equals(value);
                }
                case NOT_CONTAINS -> {
                    return !field.toString().contains(value.toString());
                }
                case NOT_STARTS_WITH -> {
                    return !field.toString().startsWith(value.toString());
                }
                case NOT_ENDS_WITH -> {
                    return !field.toString().endsWith(value.toString());
                }
                case NOT_GREATER_THAN -> {
                    return !(((Number) field).doubleValue() > ((Number) value).doubleValue());
                }
                case NOT_LESS_THAN -> {
                    return !(((Number) field).doubleValue() < ((Number) value).doubleValue());
                }
                case NOT_GREATER_THAN_OR_EQUAL_TO -> {
                    return !(((Number) field).doubleValue() >= ((Number) value).doubleValue());
                }
                case NOT_LESS_THAN_OR_EQUAL_TO -> {
                    return !(((Number) field).doubleValue() <= ((Number) value).doubleValue());
                }
                case NOT_IN -> {
                    if ((value instanceof Collection)) {
                        return !((Collection<?>) value).contains(field);
                    }
                    if (value.getClass().isArray()) {
                        return !Arrays.asList(value).contains(field);
                    }
                    if (value instanceof Map<?, ?>) {
                        return !((Map<?, ?>) value).containsKey(field) && !((Map<?, ?>) value).containsValue(field);
                    }
                    return false;
                }
                default -> {
                    return false;
                }
            }

        }
    }
}
