package wtf.casper.amethyst.paper.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import wtf.casper.amethyst.paper.AmethystFolia;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class LoggerListener extends AmethystListener<AmethystFolia> {

    public LoggerListener(AmethystFolia plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

        if (!event.getPlugin().getDescription().getDepend().contains("Amethyst")) {
            return;
        }

        event.getPlugin().getLogger().setFilter(AmethystFolia.getFilter());
    }
}
