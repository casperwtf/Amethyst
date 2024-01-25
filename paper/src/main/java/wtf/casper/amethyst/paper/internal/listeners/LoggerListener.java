package wtf.casper.amethyst.paper.internal.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class LoggerListener extends AmethystListener<JavaPlugin> {

    public LoggerListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

        if (!event.getPlugin().getDescription().getDepend().contains("Amethyst")) {
            return;
        }

        event.getPlugin().getLogger().setFilter(AmethystPaper.getFilter());
    }
}
