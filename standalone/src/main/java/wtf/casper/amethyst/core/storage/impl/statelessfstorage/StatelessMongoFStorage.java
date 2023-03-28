package wtf.casper.amethyst.core.storage.impl.statelessfstorage;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.StatelessFieldStorage;
import wtf.casper.amethyst.core.storage.id.utils.IdUtils;
import wtf.casper.amethyst.core.utils.AmethystLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatelessMongoFStorage<K, V> implements StatelessFieldStorage<K, V>, ConstructableValue<K, V> {

    private final Class<V> type;
    private final String idFieldName;
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final String collection;


    public StatelessMongoFStorage(final Class<V> type, final Credentials credentials) {
        this(credentials.getUri(), credentials.getDatabase(), credentials.getCollection(), type);
    }

    public StatelessMongoFStorage(final String uri, final String database, final String collection, final Class<V> type) {
        this.type = type;
        this.idFieldName = IdUtils.getIdName(this.type);
        MongoClientURI uri1 = new MongoClientURI(uri, MongoClientOptions.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
                ))
        );
        try {
            mongoClient = new MongoClient(uri1);
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

        this.mongoDatabase = mongoClient.getDatabase(database);
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Collection<V>> get(String field, Object value, FilterType filterType, SortingType sortingType) {
        return CompletableFuture.supplyAsync(() -> {

            Collection<V> collection = new ArrayList<>();

            Document filter = getDocument(filterType, field, convert(value));

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

            Document document = getCollection().find(new Document(idFieldName, convert(key))).first();

            if (document == null) {
                return null;
            }

            return AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
        });
    }

    @Override
    public CompletableFuture<V> getFirst(String field, Object value, FilterType filterType) {
        return CompletableFuture.supplyAsync(() -> {

            Document filter = getDocument(filterType, field, convert(value));
            Document document = getCollection().find(filter).first();

            if (document == null) {
                return null;
            }

            return AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
        });
    }

    @Override
    public CompletableFuture<Void> save(V value) {
        return CompletableFuture.runAsync(() -> {
            getCollection().replaceOne(
                    new Document(idFieldName, convert(IdUtils.getId(type, value))),
                    Document.parse(AmethystCore.getGson().toJson(value)),
                    new ReplaceOptions().upsert(true)
            );
        });
    }

    @Override
    public CompletableFuture<Void> remove(V key) {
        return CompletableFuture.runAsync(() -> {
            getCollection().deleteOne(new Document(idFieldName, convert(IdUtils.getId(type, key))));
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
        return CompletableFuture.runAsync(mongoClient::close);
    }

    @Override
    public CompletableFuture<Collection<V>> allValues() {
        return CompletableFuture.supplyAsync(() -> {
            List<Document> into = getCollection().find().into(new ArrayList<>());

            Collection<V> collection = new ArrayList<>();
            for (Document document : into) {
                V obj = AmethystCore.getGson().fromJson(document.toJson(AmethystCore.getJsonWriterSettings()), type);
                collection.add(obj);
            }

            return collection;
        });
    }

    private Document getDocument(FilterType filterType, String field, Object value) {
        Document filter;
        switch (filterType) {
            case EQUALS -> filter = new Document(field, value);
            case CONTAINS -> filter = new Document(field, new Document("$regex", value));
            case STARTS_WITH -> filter = new Document(field, new Document("$regex", "^" + value));
            case ENDS_WITH -> filter = new Document(field, new Document("$regex", value + "$"));
            case GREATER_THAN -> filter = new Document(field, new Document("$gt", value));
            case LESS_THAN -> filter = new Document(field, new Document("$lt", value));
            case GREATER_THAN_OR_EQUAL_TO -> filter = new Document(field, new Document("$gte", value));
            case LESS_THAN_OR_EQUAL_TO -> filter = new Document(field, new Document("$lte", value));
            case IN -> filter = new Document(field, new Document("$in", value));
            case NOT_EQUALS -> filter = new Document(field, new Document("$ne", value));
            case NOT_CONTAINS -> filter = new Document(field, new Document("$not", new Document("$regex", value)));
            case NOT_STARTS_WITH ->
                    filter = new Document(field, new Document("$not", new Document("$regex", "^" + value)));
            case NOT_ENDS_WITH ->
                    filter = new Document(field, new Document("$not", new Document("$regex", value + "$")));
            case NOT_IN -> filter = new Document(field, new Document("$nin", value));
            case NOT_GREATER_THAN -> filter = new Document(field, new Document("$not", new Document("$gt", value)));
            case NOT_LESS_THAN -> filter = new Document(field, new Document("$not", new Document("$lt", value)));
            case NOT_GREATER_THAN_OR_EQUAL_TO ->
                    filter = new Document(field, new Document("$not", new Document("$gte", value)));
            case NOT_LESS_THAN_OR_EQUAL_TO ->
                    filter = new Document(field, new Document("$not", new Document("$lte", value)));
            default -> throw new IllegalStateException("Unexpected value: " + filterType);
        }
        return filter;
    }

    private MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection(collection);
    }

    private Object convert(Object o) {
        if (o instanceof UUID) {
            return o.toString();
        }
        return o;
    }
}
