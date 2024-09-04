package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ArmorstandUtils extends AmethystListener<JavaPlugin> {

    private static boolean INITIALIZED = false;

    private static final NamespacedKey armorstandKey = new NamespacedKey("amethyst", "amethyst_armorstand");

    public ArmorstandUtils(JavaPlugin plugin) {
        super(plugin);
        if (INITIALIZED) {
            plugin.getLogger().warning("ArmorstandUtils has already been initialized, shutting down.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            HandlerList.unregisterAll(plugin);
            return;
        }
        INITIALIZED = true;
    }

    /**
     * Mark the armor stand, so it cannot be interacted with.
     *
     * @param armorStand The armor stand to mark
     */
    public static void markArmorstand(ArmorStand armorStand) {
        armorStand.getPersistentDataContainer().set(armorstandKey, PersistentDataType.BYTE, (byte) 1);
    }

    /**
     * Unmark the armor stand, so it can be interacted with.
     *
     * @param armorStand The armor stand to unmark
     */
    public static void unmarkArmorstand(ArmorStand armorStand) {
        armorStand.getPersistentDataContainer().remove(armorstandKey);
    }

    /**
     * Check if the armor stand is being tracked by the plugin.
     *
     * @param armorStand The armor stand to check
     * @return true if the armor stand is being tracked
     */
    public static boolean isTracked(ArmorStand armorStand) {
        return armorStand.getPersistentDataContainer().has(armorstandKey, PersistentDataType.BYTE);
    }

    /**
     * Do not call this method directly, it is used to prevent armor stands from being damaged.
     *
     * @param event
     */
    @EventHandler
    public void onArmorStandHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand && isTracked((ArmorStand) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Do not call this method directly, it is used to prevent armor stands from being interacted with.
     *
     * @param event
     */
    @EventHandler
    public void onArmorStandInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand && isTracked((ArmorStand) event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    /**
     * Do not call this method directly, it is used to prevent armor stands from being interacted with.
     *
     * @param event
     */
    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand && isTracked((ArmorStand) event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    /**
     * Do not call this method directly, it is used to prevent armor stands from being interacted with.
     *
     * @param event
     */
    @EventHandler
    public void onArmorStandVehicle(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof ArmorStand && isTracked((ArmorStand) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
