package wtf.casper.amethyst.paper.hooks.protection.protections;

import com.google.auto.service.AutoService;
import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.permissions.PermissionHelper;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

@AutoService(IProtection.class)
public class CrashClaimProtection implements IProtection {

    @Override
    public boolean canBuild(Player player, Location location) {
        return !isClaimed(location)
                || PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, location.getBlock().getType())
                && PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, PermissionRoute.BUILD);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return !isClaimed(location)
                || PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, location.getBlock().getType())
                && PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, PermissionRoute.BUILD);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return !isClaimed(location)
                || PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, location.getBlock().getType())
                && PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, PermissionRoute.INTERACTIONS);
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        return !isClaimed(location)
                || PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, location.getBlock().getType())
                && PermissionHelper.getPermissionHelper().hasPermission(player.getUniqueId(), location, PermissionRoute.ENTITIES);
    }

    @Override
    public boolean isClaimed(Location location) {
        return CrashClaim.getPlugin().getApi().getClaim(location) != null;
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getServer().getPluginManager().getPlugin("CrashClaim") != null;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public int priority() {
        return 1;
    }
}
