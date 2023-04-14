package wtf.casper.amethyst.paper.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class PlayerBlockListener extends AmethystListener<AmethystPlugin> {

    public PlayerBlockListener(AmethystPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), getPlugin());
        customBlockData.set(getPlugin().getPlayerPlacedBlockKey(), PersistentDataType.BYTE, (byte) 1);
    }
}
