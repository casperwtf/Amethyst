package wtf.casper.amethyst.paper.vault;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.amethyst.paper.events.vault.permissions.*;

import java.util.List;

/**
 * Wrapper around Permission implementation to fire events.
 *
 * @author casperwtf
 */
public class EventPermissionWrapper extends Permission {

    private final Permission original;

    public EventPermissionWrapper(Permission original) {
        this.original = original;
    }

    // returns true if not cancelled
    private <T extends Event> void callEvent(T event) {
        Bukkit.getScheduler().runTaskAsynchronously(AmethystPaper.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });
    }


    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    public boolean isEnabled() {
        return original.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return original.hasSuperPermsCompat();
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return original.playerHas(world, player, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        boolean success = original.playerAdd(world, player, permission);
        callEvent(new PlayerPermissionAddEvent(world, Bukkit.getOfflinePlayer(player), permission, success));
        return success;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        boolean success = original.playerRemove(world, player, permission);
        callEvent(new PlayerPermissionRemoveEvent(world, Bukkit.getOfflinePlayer(player), permission, success));
        return success;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return original.groupHas(world, group, permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        boolean success = original.groupAdd(world, group, permission);
        callEvent(new GroupAddPermissionEvent(world, group, permission, success));
        return success;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        boolean success = original.groupRemove(world, group, permission);
        callEvent(new GroupRemovePermissionEvent(world, group, permission, success));
        return success;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return original.playerInGroup(world, player, group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        boolean success = original.playerAddGroup(world, player, group);
        callEvent(new PlayerAddGroupEvent(world, Bukkit.getOfflinePlayer(player), group, success));
        return success;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        boolean success = original.playerRemoveGroup(world, player, group);
        callEvent(new PlayerRemoveGroupEvent(world, Bukkit.getOfflinePlayer(player), group, success));
        return success;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return original.getPlayerGroups(world, player);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return original.getPrimaryGroup(world, player);
    }

    @Override
    public String[] getGroups() {
        return original.getGroups();
    }

    @Override
    public boolean hasGroupSupport() {
        return original.hasGroupSupport();
    }
}