package wtf.casper.amethyst.velocity.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.velocitypowered.api.proxy.Player;
import io.lettuce.core.pubsub.RedisPubSubListener;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.mq.redis.RedisListener;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyMoveServerRequest;
import wtf.casper.amethyst.velocity.AmethystVelocity;

import java.util.Optional;

public class MoveServerRequestListener implements RedisPubSubListener<String, String > {

    private final AmethystVelocity plugin;

    public MoveServerRequestListener(AmethystVelocity plugin) {
        this.plugin = plugin;
    }

    public void onMessage(ProxyMoveServerRequest request) {
        Optional<Player> player = plugin.getProxy().getPlayer(request.getTarget());
        if (player.isPresent()) {
            plugin.getProxy().getServer(request.getServer()).ifPresentOrElse(
                    server -> player.get().createConnectionRequest(server).fireAndForget(),
                    () -> AmethystLogger.error("Server " + request.getServer() + " does not exist!")
            );
        } else {
            AmethystLogger.error("Player " + request.getTarget() + " does not exist!");
        }
    }

    @Override
    public void message(String channel, String message) {
        JsonObject json = AmethystCore.getGson().fromJson(message, JsonObject.class);
        if (!json.has("amethyst_class_type")) {
            return;
        }

        String type = json.get("amethyst_class_type").getAsString();
        if (!type.equals(ProxyMoveServerRequest.class.getSimpleName())) {
            return;
        }

        try {
            ProxyMoveServerRequest t = AmethystCore.getGson().fromJson(message, ProxyMoveServerRequest.class);
            if (t == null) {
                return;
            }

            this.onMessage(t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void message(String pattern, String channel, String message) {

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
}
