package wtf.casper.amethyst.paper.hooks.protection.protections;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

public class GriefPreventionProtection implements IProtection {


    @Override
    public boolean canBuild(Player player, Location location) {
        Claim claimAt = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claimAt == null) return true;
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) return true;
        return claimAt.hasExplicitPermission(player, ClaimPermission.Build);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        Claim claimAt = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claimAt == null) return true;
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) return true;
        return claimAt.hasExplicitPermission(player, ClaimPermission.Build);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        Claim claimAt = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claimAt == null) return true;
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) return true;
        return claimAt.hasExplicitPermission(player, ClaimPermission.Access);
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        Claim claimAt = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claimAt == null) return true;
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) return true;
        return claimAt.hasExplicitPermission(player, ClaimPermission.Access);
    }

    @Override
    public boolean isClaimed(Location location) {
        Claim claimAt = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        return claimAt != null;
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("GriefPrevention");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
