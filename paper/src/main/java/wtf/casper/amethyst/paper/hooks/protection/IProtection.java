package wtf.casper.amethyst.paper.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.IHook;

public interface IProtection extends IHook {
    boolean canBuild(Player player, Location location);

    boolean canBreak(Player player, Location location);

    boolean canInteract(Player player, Location location);

    boolean canAttack(Player player, Location location);

    boolean isClaimed(Location location);
}
