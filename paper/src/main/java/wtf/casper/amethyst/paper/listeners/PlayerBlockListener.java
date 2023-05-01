package wtf.casper.amethyst.paper.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public class PlayerBlockListener extends AmethystListener<JavaPlugin> {

    private final AmethystPaper amethystPaper;

    public PlayerBlockListener(JavaPlugin plugin, AmethystPaper amethystPaper) {
        super(plugin);
        this.amethystPaper = amethystPaper;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), getPlugin());
        customBlockData.set(amethystPaper.getPlayerPlacedBlockKey(), PersistentDataType.BYTE, (byte) 1);
    }
}
