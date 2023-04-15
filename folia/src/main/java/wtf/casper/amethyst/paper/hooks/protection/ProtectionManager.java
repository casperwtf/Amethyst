package wtf.casper.amethyst.paper.hooks.protection;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.protection.protections.CrashClaimProtection;
import wtf.casper.amethyst.paper.hooks.protection.protections.GriefPreventionProtection;
import wtf.casper.amethyst.paper.hooks.protection.protections.LandsProtection;
import wtf.casper.amethyst.paper.hooks.protection.protections.TownyProtection;

import java.util.ArrayList;
import java.util.List;

public class ProtectionManager {
    @Getter private static List<IProtection> protections;

    static {
        protections = new ArrayList<>();

        for (IProtection iProtection : List.of(
                new CrashClaimProtection(),
                new GriefPreventionProtection(),
                new LandsProtection(),
                new TownyProtection()
        )) {

            if (iProtection.canEnable()) {
                iProtection.enable();
                protections.add(iProtection);
            }
        }
    }

    public static void disable() {
        for (IProtection iProtection : protections) {
            iProtection.disable();
        }
    }

    public static boolean canAttack(Player player, Location location) {
        if (protections.isEmpty()) return true;
        for (IProtection protection : protections) {
            if (!protection.canAttack(player, location)) return false;
        }
        return true;
    }

    public static boolean canBuild(Player player, Location location) {
        if (protections.isEmpty()) return true;
        for (IProtection protection : protections) {
            if (!protection.canBuild(player, location)) return false;
        }
        return true;
    }

    public static boolean canInteract(Player player, Location location) {
        if (protections.isEmpty()) return true;
        for (IProtection protection : protections) {
            if (!protection.canInteract(player, location)) return false;
        }
        return true;
    }

    public static boolean canBreak(Player player, Location location) {
        if (protections.isEmpty()) return true;
        for (IProtection protection : protections) {
            if (!protection.canBreak(player, location)) return false;
        }
        return true;
    }

    public static boolean isClaimed(Location location) {
        if (protections.isEmpty()) return false;
        for (IProtection protection : protections) {
            if (protection.isClaimed(location)) return true;
        }
        return false;
    }

}
