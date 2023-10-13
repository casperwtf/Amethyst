package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.AmethystPlugin;

import java.util.*;

public class ServerLock extends AmethystListener<JavaPlugin> {

    private final static List<JavaPlugin> lockingPlugins = new ArrayList<>();
    private static boolean locked = false;
    private static Map<String, String> reason = new HashMap<>();

    public ServerLock(JavaPlugin plugin) {
        super(plugin);
    }

    public static void lock(AmethystPlugin plugin, String reason) {
        locked = true;
        lockingPlugins.add(plugin);
        ServerLock.reason.put(plugin.getName(), reason);
    }

    public static void unlock(AmethystPlugin plugin) {
        lockingPlugins.remove(plugin);
        ServerLock.reason.remove(plugin.getName());
        if (lockingPlugins.isEmpty()) locked = false;
    }


    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (!locked) return;
        Optional<String> reason = ServerLock.reason.values().stream().findFirst();

        if (reason.isPresent())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(reason.get()));
        else
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Server is locked"));
    }
}
