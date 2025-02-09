package wtf.casper.amethyst.paper.hooks.protection.protections;

import com.google.auto.service.AutoService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.protection.IProtection;

@AutoService(IProtection.class)
public class VanillaProtection implements IProtection {

    @Override
    public boolean canBuild(Player player, Location location) {
        return true;
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return true;
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return true;
    }

    @Override
    public boolean canAttack(Player player, Location location) {
        return true;
    }

    @Override
    public boolean isClaimed(Location location) {
        return false;
    }

    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
