package wtf.casper.amethyst.velocity.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import wtf.casper.amethyst.core.mq.Message;
import wtf.casper.amethyst.velocity.AmethystVelocity;

public class RedisManager {

    private final StatefulRedisPubSubConnection<String, String> connection;

    public RedisManager(AmethystVelocity plugin) {
        if (plugin.getConfig().getBoolean("mq.redis.enabled")) {
            connection = RedisClient.create(plugin.getConfig().getString("mq.redis.uri")).connectPubSub();
        } else {
            connection = null;
        }

        if (connection == null) {
            return;
        }

        connection.addListener(new MoveServerRequestListener(plugin));
    }

    public void sendRedisMessage(String channel, Message message) {
        if (connection != null && connection.isOpen()) {
            connection.async().publish(channel, message.jsonSerialize());
        }
    }
}
