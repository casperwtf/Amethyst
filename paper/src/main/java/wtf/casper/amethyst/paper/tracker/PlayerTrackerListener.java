package wtf.casper.amethyst.paper.tracker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class PlayerTrackerListener implements Listener {
    public PlayerTrackerListener(AmethystPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
