package wtf.casper.amethyst.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import wtf.casper.amethyst.core.mq.redis.RedisChannels;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyJoinMessage;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyLeaveMessage;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyMoveMessage;
import wtf.casper.amethyst.velocity.AmethystVelocity;

public class PlayerListener {

    private final AmethystVelocity plugin;

    public PlayerListener(AmethystVelocity plugin) {
        plugin.getProxy().getEventManager().register(plugin, this);
        this.plugin = plugin;
    }

    @Subscribe
    public void onServerConnect(ServerPostConnectEvent event) {
        if (event.getPreviousServer() == null) {
            plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                    new ProxyJoinMessage(event.getPlayer().getUniqueId(),
                            event.getPlayer().getUsername(),
                            event.getPlayer().getCurrentServer().get().getServerInfo().getName()));
            return;
        }

        plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                new ProxyMoveMessage(event.getPlayer().getUniqueId(),
                        event.getPlayer().getUsername(),
                        event.getPreviousServer().getServerInfo().getName(),
                        event.getPlayer().getCurrentServer().get().getServerInfo().getName()));
    }

    @Subscribe
    public void onServerDisconnect(DisconnectEvent event) {
        plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                new ProxyLeaveMessage(event.getPlayer().getUniqueId(),
                        event.getPlayer().getUsername(),
                        event.getPlayer().getCurrentServer().get().getServerInfo().getName()));
    }
}
