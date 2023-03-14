package wtf.casper.amethyst.core.storage.impl.fstorage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.SneakyThrows;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.FieldStorage;
import wtf.casper.amethyst.core.storage.id.StorageSerialized;
import wtf.casper.amethyst.core.storage.id.Transient;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.unsafe.UnsafeConsumer;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class SQLiteFStorage<K, V> implements ConstructableValue<K, V>, FieldStorage<K, V> {

    private final Connection connection;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final String table;
    private Cache<K, V> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final File file, String table) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
    }

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final String table, final String connection) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.connection = DriverManager.getConnection(connection);
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
    }

    @Override
    public Cache<K, V> cache() {
        return this.cache;
    }

    @Override
    public void cache(Cache<K, V> cache) {
        this.cache = cache;
    }

    @SneakyThrows
    public CompletableFuture<Collection<V>> get(final String field, Object value, FilterType filterType, SortingType sortingType) {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            if (!filterType.isApplicable(value.getClass())) {
                AmethystLogger.error("Filter type " + filterType.name() + " is not applicable to " + value.getClass().getSimpleName());
                return values;
            }

            switch (filterType) {
                case EQUALS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
                        if (value instanceof UUID) {
                            statement.setString(1, value.toString());
                        } else {
                            statement.setObject(1, value);
                        }

                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case CONTAINS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, "%" + value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case STARTS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case ENDS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
                        statement.setObject(1, "%" + value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case GREATER_THAN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " > ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case LESS_THAN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " < ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case GREATER_THAN_OR_EQUAL_TO -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " >= ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case LESS_THAN_OR_EQUAL_TO -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " <= ?")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case IN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " IN (?)")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_EQUALS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " != ?")) {
                        if (value instanceof UUID) {
                            statement.setString(1, value.toString());
                        } else {
                            statement.setObject(1, value);
                        }
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_CONTAINS -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, "%" + value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_STARTS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, value + "%");
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_ENDS_WITH -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
                        statement.setObject(1, "%" + value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                case NOT_IN -> {
                    try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT IN (?)")) {
                        statement.setObject(1, value);
                        final ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            values.add(this.construct(resultSet));
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return values;
        });
    }

    @Override
    public CompletableFuture<V> get(K key) {
        return getFirst(IdUtils.getIdName(this.valueClass), key);
    }

    @Override
    public CompletableFuture<V> getFirst(String field, Object value, FilterType filterType) {
        return CompletableFuture.supplyAsync(() -> {
            return this.get(field, value, filterType, SortingType.NONE).join().stream().findFirst().orElse(null);
//            try (final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
//                if (value instanceof UUID) {
//                    statement.setString(1, value.toString());
//                } else {
//                    statement.setObject(1, value);
//                }
//                final ResultSet resultSet = statement.executeQuery();
//                if (resultSet.next()) {
//                    return this.construct(resultSet);
//                }
//            } catch (final SQLException e) {
//                e.printStackTrace();
//            }
//            return null;
        });
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        return CompletableFuture.runAsync(() -> {
            Object id = IdUtils.getId(valueClass, value);

            if (id == null) {
                return;
            }

            String values = this.getValues(value);
            this.execute("INSERT OR REPLACE INTO " + this.table + " (" + this.getColumns() + ") VALUES (" + values + ")");
        });
    }

    @Override
    public CompletableFuture<Void> remove(final V value) {
        return CompletableFuture.runAsync(() -> {
            Field idField = IdUtils.getIdField(valueClass);
            if (idField == null) {
                AmethystLogger.error("Could not find id field for " + keyClass.getSimpleName());
                return;
            }
            String field = idField.getName();
            this.execute("DELETE FROM " + this.table + " WHERE " + field + " = ?;", statement -> {
                statement.setString(1, IdUtils.getId(this.valueClass, value).toString());
            });
        });
    }

    @Override
    @SneakyThrows
    public CompletableFuture<Void> write() {
        return CompletableFuture.runAsync(() -> {
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(() -> {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            try (final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + this.table)) {
                final ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    values.add(this.construct(resultSet));
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return values;
        });
    }

    private CompletableFuture<ResultSet> query(final String query, final UnsafeConsumer<PreparedStatement> statement, final UnsafeConsumer<ResultSet> result) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.connection.prepareStatement(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((prepared, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            statement.accept(prepared);
        }).thenApply(prepared -> {
            try {
                return prepared.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((set, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            result.accept(set);
        }).toCompletableFuture();
    }

    private CompletableFuture<ResultSet> query(final String query, final UnsafeConsumer<ResultSet> result) {
        return this.query(query, statement -> {
        }, result);
    }

    private void execute(final String statement) {
        this.execute(statement, ps -> {
        });
    }

    private void execute(final String statement, final UnsafeConsumer<PreparedStatement> consumer) {
        try (final PreparedStatement prepared = this.connection.prepareStatement(statement)) {
            consumer.accept(prepared);
            prepared.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    private void addColumn(final String column, final String type) {
        this.execute("ALTER TABLE " + this.table + " ADD " + column + " " + type + ";");
    }

    /**
     * Will scan the class for fields and add them to the database if they don't exist
     */
    private void scanForMissingColumns() {
        try (final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + this.table)) {
            final ResultSetMetaData metaData = statement.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }

            final List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Transient.class))
                    .filter(field -> !Modifier.isTransient(field.getModifiers()))
                    .toList();

            for (final Field field : fields) {
                final String name = field.getName();
                if (!columns.contains(name)) {
                    this.addColumn(name, this.getType(field.getType()));
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate an SQL Script to create the table based on the class
     * */
    private String createTableFromObject() {
        final StringBuilder builder = new StringBuilder();

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        if (fields.size() == 0) {
            return "";
        }

        builder.append("CREATE TABLE IF NOT EXISTS ").append(this.table).append(" (");

        String idName = IdUtils.getIdName(valueClass);

        int index = 0;
        for (Field declaredField : fields) {

            final String name = declaredField.getName();
            String type = this.getType(declaredField.getType());

            if (declaredField.isAnnotationPresent(StorageSerialized.class)) {
                type = "VARCHAR(255)";
            }

            builder.append("`" + name + "`").append(" ").append(type);
            if (name.equals(idName)) {
                builder.append(" PRIMARY KEY");
            }

            index++;

            if (index != fields.size()) {
                builder.append(", ");
            }

        }
        builder.append(");");

        AmethystLogger.debug("Generated SQL: " + builder);
        return builder.toString();
    }

    /**
     * This takes an SQL Result Set and parses it into an object
     */
    @SneakyThrows
    private V construct(final ResultSet resultSet) {
        final V value = constructValue();
        final Field[] declaredFields = this.valueClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(StorageSerialized.class)) {
                final String name = declaredField.getName();
                final String string = resultSet.getString(name);
                final Object object = AmethystCore.getGson().fromJson(string, declaredField.getType());
                declaredField.set(value, object);
                continue;
            }

            final String name = declaredField.getName();
            final Object object = resultSet.getObject(name);

            ReflectionUtil.setPrivateField(value, name, object);
        }

        return value;
    }

    /*
     * Generates an SQL String for inserting a value into the database.
     * */
    private String getValues(V value) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        Field[] fields = ReflectionUtil.getAllFields(valueClass);
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            if (field.isAnnotationPresent(StorageSerialized.class)) {
                builder.append("'").append(AmethystCore.getGson().toJson(ReflectionUtil.getPrivateField(value, field.getName()))).append("'");
            } else {
                builder.append("'").append(ReflectionUtil.getPrivateField(value, field.getName())).append("'");
            }
            if (i != fields.length - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.substring(0, builder.length() - 1);
    }

    /*
     * Generates an SQL String for the columns associated with a value class.
     * */
    private String getColumns() {
        final StringBuilder builder = new StringBuilder();
        for (final Field field : this.valueClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            builder.append(field.getName()).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }


    /*
     * Converts a Java class to an SQL type.
     * */
    private String getType(Class<?> type) {

        return switch (type.getName()) {
            case "java.lang.String" -> "VARCHAR(255)";
            case "java.lang.Integer" -> "INT";
            case "java.lang.Long" -> "BIGINT";
            case "java.lang.Boolean" -> "BOOLEAN";
            case "java.lang.Double" -> "DOUBLE";
            case "java.lang.Float" -> "FLOAT";
            case "java.lang.Short" -> "SMALLINT";
            case "java.lang.Byte" -> "TINYINT";
            case "java.lang.Character" -> "CHAR";
            case "java.lang.Object" -> "BLOB";
            default -> "VARCHAR(255)";
        };
    }
}
