package wtf.casper.amethyst.paper.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class LoggerListener extends AmethystListener<AmethystPlugin> {

    public LoggerListener(AmethystPlugin plugin) {
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
