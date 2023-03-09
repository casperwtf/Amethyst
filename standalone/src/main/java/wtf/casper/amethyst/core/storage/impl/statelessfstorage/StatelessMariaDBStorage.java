package wtf.casper.amethyst.core.storage.impl.statelessfstorage;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Collection<V>> get(final String field, Object value) {
        return CompletableFuture.supplyAsync(() -> {
            final List<V> values = new ArrayList<>();
            try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
                statement.setObject(1, value);
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


    @Override
    public CompletableFuture<V> get(K key) {
        return getFirst(IdUtils.getIdName(this.valueClass), key);
    }

    @Override
    @NotNull
    public CompletableFuture<V> getFirst(String field, Object value) {
        return CompletableFuture.supplyAsync(() -> {
            try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
                if (value instanceof UUID) {
                    statement.setString(1, value.toString());
                } else {
                    statement.setObject(1, value);
                }
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return this.construct(resultSet);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> contains(String field, Object value) {
        return CompletableFuture.supplyAsync(() -> {
            try (final PreparedStatement statement = this.ds.getConnection().prepareStatement("SELECT * FROM " + this.table + " WHERE " + field + " = ?")) {
                statement.setObject(1, value);
                final ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return false;
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
    @SneakyThrows
    public CompletableFuture<Void> write() {
        return CompletableFuture.runAsync(() -> {
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
            prepared.executeUpdate();
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
            if (declaredField.isAnnotationPresent(Transient.class)) {
                continue;
            }

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

    /*
     * Generate an SQL Script to create the table based on the class
     * */
    private String createTableFromObject() {
        final StringBuilder builder = new StringBuilder();
        final Field[] declaredFields = this.valueClass.getDeclaredFields();

        if (declaredFields.length == 0) {
            return "";
        }

        builder.append("CREATE TABLE IF NOT EXISTS ").append(this.table).append(" (");

        String idName = IdUtils.getIdName(valueClass);

        int index = 0;
        for (Field declaredField : declaredFields) {
            index++;
            if (declaredField.isAnnotationPresent(Transient.class)) {
                continue;
            }

            final String name = declaredField.getName();
            String type = this.getType(declaredField.getType());

            if (declaredField.isAnnotationPresent(StorageSerialized.class)) {
                type = "VARCHAR(255)";
            }

            builder.append(name).append(" ").append(type);
            if (name.equals(idName)) {
                builder.append(" PRIMARY KEY");
            }

            if (index != declaredFields.length) {
                builder.append(", ");
            }
        }
        builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
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
            if (declaredField.isAnnotationPresent(Transient.class)) {
                continue;
            }
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

    private boolean isFieldList(final String field) {
        try {
            Field classField = valueClass.getField(field);
            return classField.getType().isAssignableFrom(Collection.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /*
     * Converts a Java class to an SQL type.
     * */
    private String getType(Class<?> type) {
        String name;
        switch (type.getName()) {
            case "java.lang.String":
                name = "VARCHAR(255)";
                break;
            case "java.lang.Integer":
                name = "INT";
                break;
            case "java.lang.Long":
                name = "BIGINT";
                break;
            case "java.lang.Boolean":
                name = "BOOLEAN";
                break;
            case "java.lang.Double":
                name = "DOUBLE";
                break;
            case "java.lang.Float":
                name = "FLOAT";
                break;
            case "java.lang.Short":
                name = "SMALLINT";
                break;
            case "java.lang.Byte":
                name = "TINYINT";
                break;
            case "java.lang.Character":
                name = "CHAR";
                break;
            case "java.lang.Object":
                name = "BLOB";
                break;
            case "java.util.UUID":
                name = "VARCHAR(36)";
                break;
            default:
                name = "VARCHAR(255)";
                break;
        }

        return name;
    }
}