package wtf.casper.amethyst.bungee.mq;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.mq.redis.RedisListener;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyMoveServerRequest;

public class MoveServerRequestListener implements RedisPubSubListener<String, String> {

    private final Plugin plugin;

    public MoveServerRequestListener(Plugin plugin) {
        this.plugin = plugin;
    }

    public void onMessage(ProxyMoveServerRequest request) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(request.getTarget());
        if (player == null) {
            AmethystLogger.debug("Player " + request.getTarget() + " is not online");
            return;
        }

        ServerInfo info = plugin.getProxy().getServerInfo(request.getServer());
        if (info == null) {
            AmethystLogger.debug("Server " + request.getServer() + " does not exist");
            return;
        }

        player.connect(info);
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
}
