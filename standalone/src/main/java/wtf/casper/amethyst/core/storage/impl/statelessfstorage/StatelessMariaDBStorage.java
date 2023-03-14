package wtf.casper.amethyst.core.storage.impl.statelessfstorage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.StatelessFieldStorage;
import wtf.casper.amethyst.core.storage.id.StorageSerialized;
import wtf.casper.amethyst.core.storage.id.Transient;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.unsafe.UnsafeConsumer;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class StatelessMariaDBStorage<K, V> implements ConstructableValue<K, V>, StatelessFieldStorage<K, V> {
    private final HikariDataSource ds;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final String table;

    public StatelessMariaDBStorage(final Class<K> keyClass, final Class<V> valueClass, final String table, final Credentials credentials) {
        this(keyClass, valueClass, table, credentials.getHost(), credentials.getPort(), credentials.getDatabase(), credentials.getUsername(), credentials.getPassword());
    }

    @SneakyThrows
    public StatelessMariaDBStorage(final Class<K> keyClass, final Class<V> valueClass, final String table, final String host, final int port, final String database, final String username, final String password) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.table = table;
        this.ds = new HikariDataSource();
        this.ds.setMaximumPoolSize(20);
        this.ds.setDriverClassName("org.mariadb.jdbc.Driver");
        this.ds.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false");
        this.ds.addDataSourceProperty("user", username);
        this.ds.addDataSourceProperty("password", password);
        this.ds.setAutoCommit(false);
        this.execute(createTableFromObject());
        this.scanForMissingColumns();
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " > ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " < ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " >= ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " <= ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " IN (?)")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " != ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT LIKE ?")) {
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
                    try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " NOT IN (?)")) {
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
        });
    }

    @Override
    public CompletableFuture<Void> save(final V value) {
        return CompletableFuture.runAsync(() -> {
            Object id = IdUtils.getId(valueClass, value);

            if (id == null) {
                AmethystLogger.error("Could not find id field for " + keyClass.getSimpleName());
                return;
            }

            String values = this.getValues(value);
            this.execute("INSERT INTO " + this.table + " (" + this.getColumns() + ") VALUES (" + values + ") ON DUPLICATE KEY UPDATE " + values);
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
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(() -> {
            try {
                this.ds.getConnection().close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table)) {
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
                return this.ds.getConnection().prepareStatement(query);
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
        try (final PreparedStatement prepared = this.ds.getConnection().prepareStatement(statement)) {
            consumer.accept(prepared);
            prepared.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    private void addColumn(final String column, final String type) {
        this.execute("ALTER TABLE " + this.table + " ADD " + column + " " + type + ";");
    }

    /*
     * Will scan the class for fields and add them to the database if they don't exist
     * */
    private void scanForMissingColumns() {
        final Field[] declaredFields = this.valueClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {

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
            });
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

            if (index != fields.size()) {
                builder.append(", ");
            }
            index++;
        }
        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;");

        AmethystLogger.debug("Generated SQL: " + builder);
        return builder.toString();
    }

    /*
     * This takes an SQL Result Set and parses it into an object
     * */
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
                builder.append("'").append(sanitize(AmethystCore.getGson().toJson(ReflectionUtil.getPrivateField(value, field.getName())))).append("'");
            } else {
                builder.append("'").append(sanitize(ReflectionUtil.getPrivateField(value, field.getName()))).append("'");
            }
            if (i != fields.length - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * Sanitizes an object to be used in an SQL statement.
     * This is to prevent SQL injection.
     * */
    private Object sanitize(Object object) {
        if (object instanceof String) {
            return ((String) object).replace("'", "''");
        }
        return object;
    }
}