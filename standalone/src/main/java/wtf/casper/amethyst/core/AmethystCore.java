package wtf.casper.amethyst.core;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bson.json.JsonWriterSettings;
import wtf.casper.amethyst.core.gson.RuntimeTypeAdapterFactory;
import wtf.casper.amethyst.core.gson.UUIDTypeAdapter;
import wtf.casper.amethyst.core.mq.Message;
import wtf.casper.storageapi.id.Transient;
import wtf.casper.storageapi.utils.StorageAPIConstants;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmethystCore {

    @Getter private final static Map<Type, Object> adapters = new HashMap<>();
    private static final Map<Class<? extends Message>, String> messages = new HashMap<>();
    // write numbers as strings to avoid precision loss
    @Getter private final static JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    // exclude fields with @Transient annotation
    private final static ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(Transient.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz.isAnnotationPresent(Transient.class);
        }
    };
    @Getter @Setter private static Gson gson = null;


    static {
        init();
    }

    public static void init() {
        registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
    }

    public static void registerTypeAdapter(Type type, Object adapter) {
        adapters.put(type, adapter);
        recreateGson();
    }

    public static void unregisterTypeAdapter(Type type) {
        adapters.remove(type);
        recreateGson();
    }

    private static void recreateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.addSerializationExclusionStrategy(exclusionStrategy);

        if (!messages.isEmpty()) {
            gson = gsonBuilder.create();
            RuntimeTypeAdapterFactory<Message> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory.of(Message.class);
            messages.forEach(runtimeTypeAdapterFactory::registerSubtype);
            gsonBuilder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
        }

        if (!adapters.isEmpty()) {
            adapters.forEach(gsonBuilder::registerTypeAdapter);
        }

        gson = gsonBuilder.create();
        StorageAPIConstants.setGson(gson);
    }

    public static void typeFactoryMessage(Class<? extends Message> clazz) {
        String[] split = clazz.getSimpleName().split("\\.");
        messages.put(clazz, split[split.length - 1]);
        recreateGson();
    }

    public static void removeTypeFactoryMessage(Class<? extends Message> clazz) {
        messages.remove(clazz);
        recreateGson();
    }
}
