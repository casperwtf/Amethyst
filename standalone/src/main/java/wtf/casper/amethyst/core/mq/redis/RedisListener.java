package wtf.casper.amethyst.core.mq.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.mq.Message;
import wtf.casper.amethyst.core.utils.AmethystLogger;

public abstract class RedisListener<T> implements RedisPubSubListener<String, String> {

    private final Class<T> clazz;

    public RedisListener(Class<T> clazz) {
        this.clazz = clazz;
        if (clazz.isAssignableFrom(Message.class)) {
            try {
                Class<? extends Message> aClass = (Class<? extends Message>) clazz;

                AmethystCore.typeFactoryMessage(aClass);
            } catch (ClassCastException ignored) {
                AmethystLogger.debug("Failed to register " + clazz.getName() + " as they are not an instance of " + Message.class.getName());
            }
        } else {
            AmethystLogger.debug("Failed to register " + clazz.getName() + " as they are not an instance of " + Message.class.getName());
        }
    }

    @Override
    public void message(String channel, String message) {

        JsonObject json = AmethystCore.getGson().fromJson(message, JsonObject.class);
        if (!json.has("amethyst_class_type")) {
            AmethystLogger.debug("Failed to parse message as it does not contain amethyst_class_type");
            return;
        }

        String type = json.get("amethyst_class_type").getAsString();
        if (!type.equals(clazz.getSimpleName())) {
            AmethystLogger.debug("Failed to parse message type " + type + " as it does not match " + clazz.getSimpleName());
            return;
        }

        try {
            T t = AmethystCore.getGson().fromJson(message, this.clazz);
            if (t == null) {
                AmethystLogger.debug("Failed to parse message as it is null");
                return;
            }

            this.onMessage(t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void message(String pattern, String channel, String message) {
        message(channel, message);
    }

    @Override
    public void subscribed(String channel, long count) {

    }

    @Override
    public void psubscribed(String pattern, long count) {

    }

    @Override
    public void unsubscribed(String channel, long count) {

    }

    @Override
    public void punsubscribed(String pattern, long count) {

    }

    public abstract void onMessage(T t);
}
