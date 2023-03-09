package wtf.casper.amethyst.bungee.mq;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import wtf.casper.amethyst.bungee.AmethystBungee;
import wtf.casper.amethyst.core.mq.Message;

public class RedisManager {

    private final StatefulRedisPubSubConnection<String, String> connection;

    public RedisManager(AmethystBungee plugin) {
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
            connection.async().publish(channel, message.serialize());
        }
    }
}
