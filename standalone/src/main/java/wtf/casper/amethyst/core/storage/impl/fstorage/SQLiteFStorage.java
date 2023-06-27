package wtf.casper.amethyst.core.storage.impl.fstorage;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.cache.Cache;
import wtf.casper.amethyst.core.cache.CaffeineCache;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.FieldStorage;
import wtf.casper.amethyst.core.storage.id.StorageSerialized;
import wtf.casper.amethyst.core.storage.id.Transient;
import wtf.casper.amethyst.core.storage.id.exceptions.IdNotFoundException;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.unsafe.UnsafeConsumer;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class SQLiteFStorage<K, V> implements ConstructableValue<K, V>, FieldStorage<K, V> {

    private final HikariDataSource ds;
    protected final Class<K> keyClass;
    protected final Class<V> valueClass;
    private final String table;
    private Cache<K, V> cache = new CaffeineCache<>(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build());

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final File file, String table) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.ds = new HikariDataSource();
        this.ds.setMaximumPoolSize(20);
        this.ds.setDriverClassName("org.sqlite.JDBC");
        this.ds.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
        this.ds.setConnectionTimeout(120000);
        this.ds.setLeakDetectionThreshold(300000);
        this.ds.setAutoCommit(true);
        this.execute(createTableFromObject());
        this.scanForMissingColumns();

        this.execute(createTableFromObject());
        this.scanForMissingColumns();
    }

    @SneakyThrows
    public SQLiteFStorage(final Class<K> keyClass, final Class<V> valueClass, final String table, final String connection) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.ds = new HikariDataSource();
        this.ds.setMaximumPoolSize(20);
        this.ds.setDriverClassName("org.sqlite.JDBC");
        this.ds.setJdbcUrl(connection);
        this.ds.setConnectionTimeout(120000);
        this.ds.setLeakDetectionThreshold(300000);
        this.ds.setAutoCommit(true);
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
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
                case EQUALS -> this._equals(field, value, values);
                case CONTAINS -> this._contains(field, value, values);
                case STARTS_WITH -> this.startsWith(field, value, values);
                case ENDS_WITH -> this.endsWith(field, value, values);
                case GREATER_THAN -> this.greaterThan(field, value, values);
                case LESS_THAN -> this.lessThan(field, value, values);
                case GREATER_THAN_OR_EQUAL_TO -> this.greaterThanOrEqualTo(field, value, values);
                case LESS_THAN_OR_EQUAL_TO -> this.lessThanOrEqualTo(field, value, values);
                case IN -> this.in(field, value, values);
                case NOT_EQUALS -> this.notEquals(field, value, values);
                case NOT_CONTAINS -> this.notContains(field, value, values);
                case NOT_STARTS_WITH -> this.notStartsWIth(field, value, values);
                case NOT_ENDS_WITH -> this.notEndsWith(field, value, values);
                case NOT_IN -> this.notIn(field, value, values);
            }

            for (V v : values) {
                cache.put((K) IdUtils.getId(valueClass, v), v);
            }

            return values;
        });
    }

    @Override
    public CompletableFuture<V> get(K key) {
        if (cache.getIfPresent(key) != null) {
            return CompletableFuture.completedFuture(cache.getIfPresent(key));
        }
        return getFirst(IdUtils.getIdName(this.valueClass), key);
    }

    @Override
    public CompletableFuture<V> getFirst(String field, Object value, FilterType filterType) {
        return CompletableFuture.supplyAsync(() ->
                this.get(field, value, filterType, SortingType.NONE).join().stream().findFirst().orElse(null)
        );
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        return CompletableFuture.runAsync(() -> {
            if (this.ds.isClosed()) {
                return;
            }
            Object id = IdUtils.getId(valueClass, value);
            if (id == null) {
                AmethystLogger.error("Could not find id field for " + keyClass.getSimpleName());
                return;
            }

            cache.put((K) id, value);

            String values = this.getValues(value);
            this.executeUpdate("INSERT INTO " + this.table + " VALUES (" + values + ") ON CONFLICT(" + IdUtils.getIdName(valueClass) + ") DO UPDATE SET " + this.getUpdateValues() + ";");
        });
    }

    @Override
    public CompletableFuture<Void> remove(final V value) {
        return CompletableFuture.runAsync(() -> {
            Field idField;
            try {
                idField = IdUtils.getIdField(valueClass);
            } catch (IdNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.cache.invalidate((K) IdUtils.getId(this.valueClass, value));
            String field = idField.getName();
            this.execute("DELETE FROM " + this.table + " WHERE " + field + " = '" + IdUtils.getId(this.valueClass, value) + "';");
        });
    }

    @Override
    @SneakyThrows
    public CompletableFuture<Void> write() {
        return CompletableFuture.runAsync(() -> {
            this.saveAll(this.cache.asMap().values());
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(this.ds::close);
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            query("SELECT * FROM " + this.table, statement -> {
            }, resultSet -> {
                try {
                    while (resultSet.next()) {
                        values.add(this.construct(resultSet));
                    }
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });

            return values;
        });
    }

    private CompletableFuture<ResultSet> query(final String query, final UnsafeConsumer<PreparedStatement> statement, final UnsafeConsumer<ResultSet> result) {
        return CompletableFuture.supplyAsync(() -> {
            try (final Connection connection = this.ds.getConnection()) {
                try (final PreparedStatement prepared = connection.prepareStatement(query)) {
                    statement.accept(prepared);
                    final ResultSet resultSet = prepared.executeQuery();
                    result.accept(resultSet);
                    return resultSet;
                } catch (final SQLException e) {
                    AmethystLogger.error("Error while executing query: " + query);
                    e.printStackTrace();
                }
            } catch (final SQLException e) {
                AmethystLogger.error("Error while executing query: " + query);
                e.printStackTrace();
            }
            return null;
        });
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

        try (final Connection connection = this.ds.getConnection()) {
            try (final PreparedStatement prepared = connection.prepareStatement(statement)) {
                consumer.accept(prepared);
                prepared.execute();
            } catch (final SQLException e) {
                AmethystLogger.error("Error while executing query: " + statement);
                e.printStackTrace();
            }
        } catch (final SQLException e) {
            AmethystLogger.error("Error while executing query: " + statement);
            e.printStackTrace();
        }
    }

    private void executeQuery(final String statement) {
        this.executeQuery(statement, ps -> {
        });
    }

    private void executeQuery(final String statement, final UnsafeConsumer<PreparedStatement> consumer) {
        try (final Connection connection = this.ds.getConnection()) {
            try (final PreparedStatement prepared = connection.prepareStatement(statement)) {
                consumer.accept(prepared);
                prepared.executeQuery();
            } catch (final SQLException e) {
                AmethystLogger.error("Error while executing query: " + statement);
                e.printStackTrace();
            }
        } catch (final SQLException e) {
            AmethystLogger.error("Error while executing query: " + statement);
            e.printStackTrace();
        }
    }

    private void executeUpdate(final String statement) {
        this.executeUpdate(statement, ps -> {
        });
    }

    private void executeUpdate(final String statement, final UnsafeConsumer<PreparedStatement> consumer) {
        try (final Connection connection = this.ds.getConnection()) {
            try (final PreparedStatement prepared = connection.prepareStatement(statement)) {
                consumer.accept(prepared);
                prepared.executeUpdate();
            } catch (final SQLException e) {
                AmethystLogger.error("Error while executing query: " + statement);
                e.printStackTrace();
            }
        } catch (final SQLException e) {
            AmethystLogger.error("Error while executing query: " + statement);
            e.printStackTrace();
        }
    }

    private void addColumn(final String column, final String type) {
        this.execute("ALTER TABLE " + this.table + " ADD COLUMN " + column + " " + type + ";");
    }

    /**
     * Will scan the class for fields and add them to the database if they don't exist
     */
    private void scanForMissingColumns() {
        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (Field declaredField : fields) {
            final String name = declaredField.getName();
            final String type = this.getType(declaredField.getType());

            this.query("SELECT * FROM " + this.table + " LIMIT 1;", resultSet -> {
                try {
                    if (resultSet.findColumn(name) == 0) {
                        this.addColumn(name, type);
                    }
                } catch (SQLException e) {
                    this.addColumn(name, type);
                }

                resultSet.close();
            });
        }
    }

    /**
     * Generate an SQL Script to create the table based on the class
     */
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
                type = "JSON";
            }

            builder.append("`").append(name).append("`").append(" ").append(type);
            if (name.equals(idName)) {
                builder.append(" PRIMARY KEY");
            }

            index++;

            if (index != fields.size()) {
                builder.append(", ");
            }

        }
        builder.append(");");
        return builder.toString();
    }

    /**
     * This takes an SQL Result Set and parses it into an object
     */
    @SneakyThrows
    private V construct(final ResultSet resultSet) {
        final V value = constructValue();
        List<Field> declaredFields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(StorageSerialized.class)) {
                final String name = declaredField.getName();
                final String string = resultSet.getString(name);
                final Object object = AmethystCore.getGson().fromJson(string, declaredField.getType());
                declaredField.setAccessible(true);
                declaredField.set(value, object);
                continue;
            }

            final String name = declaredField.getName();
            final Object object = resultSet.getObject(name);

            if (declaredField.getType() == UUID.class && object instanceof String) {
                ReflectionUtil.setPrivateField(value, name, UUID.fromString((String) object));
                continue;
            } else if (declaredField.getType().isEnum() && object instanceof String) {
                Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>) declaredField.getType(), (String) object);
                ReflectionUtil.setPrivateField(value, name, enumValue);
                continue;
            }

            ReflectionUtil.setPrivateField(value, name, object);
        }

        return value;
    }

    private String getUpdateValues() {
        final StringBuilder builder = new StringBuilder();
        int i = 0;

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (final Field field : fields) {
            builder.append("`").append(field.getName()).append("` = excluded.`").append(field.getName()).append("`");

            if (i != fields.size() - 1) {
                builder.append(", ");
            }

            i++;
        }

        return builder.toString();
    }

    /**
     * Generates an SQL String for the columns associated with a value class.
     */
    private String getColumns() {
        final StringBuilder builder = new StringBuilder();

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (final Field field : fields) {
            builder.append("`" + field.getName() + "`").append(",");
        }

        return builder.substring(0, builder.length() - 1);
    }


    /**
     * Converts a Java class to an SQL type.
     */
    private String getType(Class<?> type) {
        return switch (type.getName()) {
            case "java.lang.String" -> "VARCHAR(255)";
            case "java.lang.Integer", "int" -> "INT";
            case "java.lang.Long", "long" -> "BIGINT";
            case "java.lang.Boolean", "boolean" -> "BOOLEAN";
            case "java.lang.Double", "double" -> "DOUBLE";
            case "java.lang.Float", "float" -> "FLOAT";
            case "java.lang.Short", "short" -> "SMALLINT";
            case "java.lang.Byte", "byte" -> "TINYINT";
            case "java.lang.Character", "char" -> "CHAR";
            case "java.util.UUID" -> "VARCHAR(36)";
            default -> "VARCHAR(255)";
        };
    }

    /**
     * Generates an SQL String for inserting a value into the database.
     */
    private String getValues(V value) {
        final StringBuilder builder = new StringBuilder();
        int i = 0;

        List<Field> fields = Arrays.stream(this.valueClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toList();

        for (final Field field : fields) {
            Object privateField = ReflectionUtil.getPrivateField(value, field.getName());
            if (field.isAnnotationPresent(StorageSerialized.class)) {
                builder.append("'").append(AmethystCore.getGson().toJson(privateField)).append("'");
            } else {

                boolean shouldHaveQuotes = shouldHaveQuotes(privateField);
                if (shouldHaveQuotes) {
                    builder.append("'");
                }
                if (privateField instanceof Map<?,?> map) {
                    if (map.isEmpty()) {
                        builder.append("NULL");
                    }
                } else if (privateField instanceof List<?> list) {
                    if (list.isEmpty()) {
                        builder.append("''");
                    }
                } else {
                    builder.append(privateField);
                }

                if (shouldHaveQuotes) {
                    builder.append("'");
                }
            }
            if (i != fields.size() - 1) {
                builder.append(", ");
            }
            i++;
        }

        return builder.toString();
    }

    private boolean shouldHaveQuotes(Object value) {
        if (value instanceof Enum<?>) {
            return true;
        }
        return switch (value.getClass().getName()) {
            case "java.lang.String", "java.util.UUID" -> true;
            default -> false;
        };
    }

    private void setStatement(PreparedStatement statement, int i, Object value) {
        switch (value.getClass().getSimpleName()) {
            case "String" -> {
                try {
                    statement.setString(i, (String) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Integer", "int" -> {
                try {
                    statement.setInt(i, (Integer) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Long", "long" -> {
                try {
                    statement.setLong(i, (Long) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Boolean", "boolean" -> {
                try {
                    statement.setBoolean(i, (Boolean) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Double", "double" -> {
                try {
                    statement.setDouble(i, (Double) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Float", "float" -> {
                try {
                    statement.setFloat(i, (Float) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Short", "short" -> {
                try {
                    statement.setShort(i, (Short) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Byte", "byte" -> {
                try {
                    statement.setByte(i, (Byte) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Character", "char" -> {
                try {
                    statement.setString(i, String.valueOf(value));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "UUID" -> {
                try {
                    statement.setString(i, value.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Timestamp" -> {
                try {
                    statement.setTimestamp(i, (Timestamp) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Date" -> {
                try {
                    statement.setDate(i, (Date) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Time" -> {
                try {
                    statement.setTime(i, (Time) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "BigDecimal" -> {
                try {
                    statement.setBigDecimal(i, (BigDecimal) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Blob" -> {
                try {
                    statement.setBlob(i, (Blob) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Clob" -> {
                try {
                    statement.setClob(i, (Clob) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Array" -> {
                try {
                    statement.setArray(i, (Array) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "Ref" -> {
                try {
                    statement.setRef(i, (Ref) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "NClob" -> {
                try {
                    statement.setNClob(i, (NClob) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "NString" -> {
                try {
                    statement.setNString(i, (String) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "RowId" -> {
                try {
                    statement.setRowId(i, (RowId) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "SQLXML" -> {
                try {
                    statement.setSQLXML(i, (SQLXML) value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            default -> {
                try {
                    statement.setObject(i, value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void _equals(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " = ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void _contains(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, "%" + value + "%");
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void startsWith(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value + "%");
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void endsWith(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, "%" + value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void greaterThan(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " > ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void lessThan(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " < ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void greaterThanOrEqualTo(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " >= ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void lessThanOrEqualTo(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " <= ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void in(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " IN (?)", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void notEquals(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " != ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void notContains(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, "%" + value + "%");
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void notStartsWIth(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, value + "%");
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void notEndsWith(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?", preparedStatement -> {
            setStatement(preparedStatement, 1, "%" + value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }

    private void notIn(String field, Object value, List<V> values) {
        this.query("SELECT * FROM " + this.table + " WHERE " + field + " NOT IN (?)", preparedStatement -> {
            setStatement(preparedStatement, 1, value);
        }, resultSet -> {
            while (resultSet.next()) {
                values.add(this.construct(resultSet));
            }
            resultSet.close();
        }).join();
    }
}
