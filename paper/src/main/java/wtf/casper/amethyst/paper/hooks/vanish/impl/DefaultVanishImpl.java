package wtf.casper.amethyst.paper.hooks.vanish.impl;

import com.google.auto.service.AutoService;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.vanish.IVanish;

@AutoService(IVanish.class)
public class DefaultVanishImpl implements IVanish {

    @Override
    public boolean isVanished(Player player) {
        return player.hasMetadata("vanished");
    }

    @Override
    public void setVanished(Player player, boolean vanished) {
        throw new RuntimeException("Missing run-time dependency to handle IVanish#setVanished(Player, boolean)");
    }

    @Override
    public long getVanishPriority(Player player) {
        return 0;
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
