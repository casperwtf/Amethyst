package wtf.casper.amethyst.paper.hooks.protection.protections;

import com.google.auto.service.AutoService;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

@AutoService(IProtection.class)
public class LandsProtection implements IProtection {
    private LandsIntegration landsIntegration;

    @Override
    public boolean canBuild(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

    @Override
    public boolean canInteract(Player player, Location location) {

        if (!landsIntegration.isClaimed(location)) {
            return true;
        }

        Area areaByLoc = landsIntegration.getAreaByLoc(location);

        if (areaByLoc == null) {
            return true;
        }

        if (location.getBlock().getState() instanceof InventoryHolder) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_CONTAINER);
        }
        if (location.getBlock().getState() instanceof Door) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_DOOR);
        }
        if (location.getBlock().getState() instanceof TrapDoor) {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_TRAPDOOR);
        } else {
            return areaByLoc.hasFlag(player.getUniqueId(), Flags.INTERACT_GENERAL);
        }
    }

    @Override
    public boolean isClaimed(Location location) {
        return landsIntegration.isClaimed(location);
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        return !landsIntegration.isClaimed(location) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.ATTACK_ANIMAL) ||
                landsIntegration.getAreaByLoc(location).hasFlag(player.getUniqueId(), Flags.ATTACK_MONSTER);
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getServer().getPluginManager().getPlugin("Lands") != null;
    }

    @Override
    public void enable() {
        landsIntegration = new LandsIntegration(AmethystPaper.getInstance());
    }

    @Override
    public void disable() {

    }

    @Override
    public int priority() {
        return 1;
    }
}
