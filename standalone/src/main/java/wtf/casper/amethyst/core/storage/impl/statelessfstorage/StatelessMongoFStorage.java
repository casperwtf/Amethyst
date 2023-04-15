package wtf.casper.amethyst.core.storage.impl.statelessfstorage;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.cache.Cache;
import wtf.casper.amethyst.core.cache.CaffeineCache;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.MongoProvider;
import wtf.casper.amethyst.core.storage.StatelessFieldStorage;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.utils.AmethystLogger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class StatelessMongoFStorage<K, V> implements StatelessFieldStorage<K, V>, ConstructableValue<K, V> {

    private final Class<V> type;
    private final String idFieldName;
    private final MongoClient mongoClient;
    @Getter
    private final MongoCollection<Document> collection;

    public StatelessMongoFStorage(final Class<V> type, final Credentials credentials) {
        this(credentials.getUri(), credentials.getDatabase(), credentials.getCollection(), type);
    }

    public StatelessMongoFStorage(final String uri, final String database, final String collection, final Class<V> type) {
        this.type = type;
        this.idFieldName = IdUtils.getIdName(this.type);
        try {
            AmethystLogger.debug("Connecting to MongoDB...");
            mongoClient = MongoProvider.getClient(uri);
        } catch (Exception e) {
            AmethystLogger.warning(" ");
            AmethystLogger.warning(" ");
            AmethystLogger.warning("Failed to connect to MongoDB. Please check your credentials.");
            AmethystLogger.warning(" ");
            AmethystLogger.warning(" ");
            AmethystLogger.warning("Developer Stack Trace: ");
            AmethystLogger.warning(" ");
            throw e;
        }

        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        this.collection = mongoDatabase.getCollection(collection);
    }

    @Override
    public CompletableFuture<Collection<V>> get(String field, Object value, FilterType filterType, SortingType sortingType) {
        return CompletableFuture.supplyAsync(() -> {

            Collection<V> collection = new ArrayList<>();
            Bson filter = getDocument(filterType, field, value);
            List<Document> into = getCollection().find(filter).into(new ArrayList<>());

            for (Document document : into) {
                V obj = AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
                collection.add(obj);
            }

            return sortingType.sort(collection, field);
        });
    }

    @Override
    public CompletableFuture<V> get(K key) {
        return CompletableFuture.supplyAsync(() -> {
            Document filter = new Document(idFieldName, convertUUIDtoString(key));
            Document document = getCollection().find(filter).first();

            if (document == null) {
                return null;
            }

            V obj = AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
            return obj;
        });
    }

    @Override
    public CompletableFuture<V> getFirst(String field, Object value, FilterType filterType) {
        return CompletableFuture.supplyAsync(() -> {
            Bson filter = getDocument(filterType, field, value);
            Document document = getCollection().find(filter).first();

            if (document == null) {
                return null;
            }

            V obj = AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
            K key = (K) IdUtils.getId(type, obj);
            return obj;
        });
    }

    @Override
    public CompletableFuture<Void> save(V value) {
        return CompletableFuture.runAsync(() -> {
            K key = (K) IdUtils.getId(type, value);
            getCollection().replaceOne(
                    new Document(idFieldName, convertUUIDtoString(key)),
                    Document.parse(AmethystCore.getGson().toJson(value)),
                    new ReplaceOptions().upsert(true)
            );
        });
    }

    @Override
    public CompletableFuture<Void> remove(V key) {
        return CompletableFuture.runAsync(() -> {
            try {
                K id = (K) IdUtils.getId(type, key);
                getCollection().deleteMany(getDocument(FilterType.EQUALS, idFieldName, convertUUIDtoString(id)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> write() {
        // No need to write to mongo
        return CompletableFuture.runAsync(() -> {
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        // No need to close mongo because it's handled by a provider
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            List<Document> into = getCollection().find().into(new ArrayList<>());
            List<V> collection = new ArrayList<>();

            for (Document document : into) {
                V obj = AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
                collection.add(obj);
            }

            return collection;
        });
    }

    private Bson getDocument(FilterType filterType, String field, Object value) {
        value = convertUUIDtoString(value);
        Bson filter;
        switch (filterType) {
            case EQUALS -> filter = Filters.eq(field, value);
            case NOT_EQUALS -> filter = Filters.ne(field, value);
            case GREATER_THAN -> filter = Filters.gt(field, value);
            case LESS_THAN -> filter = Filters.lt(field, value);
            case GREATER_THAN_OR_EQUAL_TO -> filter = Filters.gte(field, value);
            case LESS_THAN_OR_EQUAL_TO -> filter = Filters.lte(field, value);
            case CONTAINS -> filter = Filters.regex(field, value.toString());
            case STARTS_WITH -> filter = Filters.regex(field, "^" + value.toString());
            case ENDS_WITH -> filter = Filters.regex(field, value.toString() + "$");
            case IN -> filter = Filters.in(field, (List<?>) value);
            case NOT_IN -> filter = Filters.nin(field, (List<?>) value);
            case NOT_CONTAINS -> filter = Filters.not(Filters.regex(field, value.toString()));
            case NOT_STARTS_WITH -> filter = Filters.not(Filters.regex(field, "^" + value.toString()));
            case NOT_ENDS_WITH -> filter = Filters.not(Filters.regex(field, value.toString() + "$"));
            case NOT_GREATER_THAN -> filter = Filters.not(Filters.gt(field, value));
            case NOT_LESS_THAN -> filter = Filters.not(Filters.lt(field, value));
            case NOT_GREATER_THAN_OR_EQUAL_TO -> filter = Filters.not(Filters.gte(field, value));
            case NOT_LESS_THAN_OR_EQUAL_TO -> filter = Filters.not(Filters.lte(field, value));
            default -> throw new IllegalStateException("Unexpected value: " + filterType);
        }
        return filter;
    }

    private Object convertUUIDtoString(Object object) {
        if (object instanceof UUID) {
            return object.toString();
        }

        if (object instanceof List) {
            List<Object> list = new ArrayList<>();
            for (Object o : (List<?>) object) {
                list.add(convertUUIDtoString(o));
            }
            return list;
        }

        if (object instanceof Map) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                map.put(entry.getKey().toString(), convertUUIDtoString(entry.getValue()));
            }
            return map;
        }

        return object;
    }
}
