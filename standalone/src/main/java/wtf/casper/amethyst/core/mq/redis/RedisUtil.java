package wtf.casper.amethyst.core.mq.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

@Deprecated
public class RedisUtil {

    public static RedisClient createRedisClient(String uri) {
        return RedisClient.create(uri);
    }

    public static StatefulRedisPubSubConnection<String, String> createRedisPubSubConnection(RedisClient redisClient) {
        return redisClient.connectPubSub();
    }
}