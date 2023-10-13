package wtf.casper.amethyst.paper.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.events.PlayerSmeltItemEvent;
import wtf.casper.amethyst.paper.scheduler.SchedulerUtil;
import wtf.casper.amethyst.paper.utils.AmethystListener;

import java.util.UUID;

public class PlayerSmeltItemEventListener extends AmethystListener<JavaPlugin> {

    public PlayerSmeltItemEventListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        Block block = event.getBlock();

        PersistentDataContainer customBlockData = new CustomBlockData(block, getPlugin());

        String key = customBlockData.get(AmethystPaper.getPlayerSmeltItemKey(), PersistentDataType.STRING);

        if (key == null) {
            return;
        }

        String[] keySplit = key.split(":");

        if (keySplit.length != 3) {
            return;
        }

        OfflinePlayer player = getPlugin().getServer().getOfflinePlayer(UUID.fromString(keySplit[0]));
        Material material = Material.valueOf(keySplit[1]);
        int amount = Integer.parseInt(keySplit[2]);

        if (amount <= 1) {
            customBlockData.remove(AmethystPaper.getPlayerSmeltItemKey());
        } else {
            customBlockData.set(AmethystPaper.getPlayerSmeltItemKey(), PersistentDataType.STRING,
                    makeKey(player, material, amount - 1));
        }

        PlayerSmeltItemEvent itemEvent = new PlayerSmeltItemEvent(player, event.getBlock(), event.getSource(), event.getResult());
        Bukkit.getPluginManager().callEvent(itemEvent);
        event.setResult(itemEvent.getResult());
    }


    @EventHandler(ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (!(event.getView().getTopInventory() instanceof FurnaceInventory furnaceInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof OfflinePlayer player)) {
            return;
        }

        SchedulerUtil.run(() -> {
            ItemStack smelted = furnaceInventory.getItem(0);
            setItem(smelted, player, furnaceInventory);
        }, null);
    }


    private void setItem(ItemStack smelted, OfflinePlayer player, FurnaceInventory inventory) {
        Block block;

        try {
            if (inventory.getHolder() == null) {
                return;
            }
            block = inventory.getHolder().getBlock();
        } catch (Exception e) {
            return;
        }

        PersistentDataContainer customBlockData = new CustomBlockData(block, getPlugin());

        if (smelted == null) {
            customBlockData.remove(AmethystPaper.getPlayerSmeltItemKey());
            return;
        }

        // Key Format: UUID:Material:Int amount
        customBlockData.set(AmethystPaper.getPlayerSmeltItemKey(), PersistentDataType.STRING,
                makeKey(player, smelted.getType(), smelted.getAmount()));
    }

    private String makeKey(OfflinePlayer player, Material material, int amount) {
        return player.getUniqueId() + ":" + material.toString() + ":" + amount;
    }
}
