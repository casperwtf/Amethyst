package wtf.casper.amethyst.paper.tracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class PlayerTrackerListener extends AmethystListener<JavaPlugin> {
    public PlayerTrackerListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerTracker.getCache().put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerTracker.getCache().remove(event.getPlayer().getUniqueId());
    }
}
