package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ServerLock extends AmethystListener<JavaPlugin> {

    private final static Set<JavaPlugin> lockingPlugins = new HashSet<>();
    private static boolean locked = false;
    private static Map<String, String> reason = new HashMap<>();

    public ServerLock(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Locks the server with a reason. If no reason is provided, the default reason is "Server is locked".
     * Multiple locks can be applied by different plugins, but the server will only be unlocked when all locks are removed.
     * While locked, all players will be kicked with the reason provided.
     *
     * @param plugin The plugin that is locking the server
     * @param reason The reason for locking the server
     */
    public static void lock(JavaPlugin plugin, String reason) {
        locked = true;
        lockingPlugins.add(plugin);
        ServerLock.reason.put(plugin.getName(), reason);
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(Component.text(reason)));
    }

    /**
     * Unlocks the server. If multiple plugins have locked the server, the server will only be unlocked when all locks are removed.
     *
     * @param plugin
     */
    public static void unlock(JavaPlugin plugin) {
        lockingPlugins.remove(plugin);
        ServerLock.reason.remove(plugin.getName());
        if (lockingPlugins.isEmpty()) locked = false;
    }


    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (!locked) return;
        Optional<String> reason = Optional.ofNullable(ServerLock.reason.get(lockingPlugins.iterator().next().getName()));

        if (reason.isPresent()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(reason.get()));
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Server is locked"));
        }
    }
}
