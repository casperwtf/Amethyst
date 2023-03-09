package wtf.casper.amethyst.paper.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class PluginListener extends AmethystListener<AmethystPaper> {

    public PluginListener(AmethystPaper plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

        if (!event.getPlugin().getDescription().getDepend().contains("Amethyst")) {
            return;
        }

        event.getPlugin().getLogger().setFilter(AmethystPaper.getFilter());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), getPlugin());
        customBlockData.set(getPlugin().getPlayerPlacedBlockKey(), PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), getPlugin());
        if (customBlockData.has(getPlugin().getPlayerPlacedBlockKey(), PersistentDataType.BYTE)) {
            customBlockData.remove(getPlugin().getPlayerPlacedBlockKey());
        }
    }

}
