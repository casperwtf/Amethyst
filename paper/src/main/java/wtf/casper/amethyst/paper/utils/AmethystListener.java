package wtf.casper.amethyst.paper.utils;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A simple listener class that registers itself to the plugin's event manager
 *
 * @param <T> Plugin instance
 */
@Getter
public class AmethystListener<T extends JavaPlugin> implements Listener {

    protected final T plugin;

    public AmethystListener(T plugin) {
        this.plugin = plugin;
        getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }
}
