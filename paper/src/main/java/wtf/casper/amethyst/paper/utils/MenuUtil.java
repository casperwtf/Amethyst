package wtf.casper.amethyst.paper.utils;

import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.InventoryOpenerType;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.AmethystPaper;

public class MenuUtil {

    public static RyseInventory getInventory(JavaPlugin plugin, InventoryProvider provider, int size, String title, InventoryOpenerType type) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin is not enabled");
        }

        return RyseInventory.builder()
                .provider(provider)
                .type(type)
                .size(size)
                .title(title)
                .build(plugin, AmethystPaper.getInstance().getInventoryManager(plugin));
    }

    public static RyseInventory getInventory(JavaPlugin plugin, InventoryProvider provider, int size, String title) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException("Plugin is not enabled");
        }
        return RyseInventory.builder()
                .provider(provider)
                .size(size)
                .title(title)
                .build(plugin, AmethystPaper.getInstance().getInventoryManager(plugin));
    }
}
