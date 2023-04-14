package wtf.casper.amethyst.paper.hooks.protection.protections;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

public class TownyProtection implements IProtection {
    @Override
    public boolean canEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("Towny");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.DESTROY);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        return TownyAPI.getInstance().isPVP(location);
    }

    @Override
    public boolean isClaimed(Location location) {
        return !TownyAPI.getInstance().isWilderness(location);
    }
}
