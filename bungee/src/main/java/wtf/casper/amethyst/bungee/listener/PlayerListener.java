package wtf.casper.amethyst.bungee.listener;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import wtf.casper.amethyst.bungee.AmethystBungee;
import wtf.casper.amethyst.core.mq.redis.RedisChannels;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyJoinMessage;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyLeaveMessage;
import wtf.casper.amethyst.minecraft.mq.messages.ProxyMoveMessage;

public class PlayerListener implements Listener {

    private final AmethystBungee plugin;

    public PlayerListener(AmethystBungee plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                new ProxyLeaveMessage(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getPlayer().getServer().getInfo().getName()));
    }

    @EventHandler
    public void onPlayerSwitchServer(ServerSwitchEvent event) {
        ServerInfo from = event.getFrom();

        if (from == null) {
            return;
        }

        plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                new ProxyMoveMessage(event.getPlayer().getUniqueId(), event.getPlayer().getName(), from.getName(), event.getPlayer().getServer().getInfo().getName()));
    }

    @EventHandler
    public void onPlayerJoinServer(ServerConnectEvent event) {
        if (event.isCancelled()) {
            return;
        }

        plugin.getRedisManager().sendRedisMessage(RedisChannels.AMETHYST_MINECRAFT_MESSAGES,
                new ProxyJoinMessage(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getPlayer().getServer().getInfo().getName()));
    }
}
