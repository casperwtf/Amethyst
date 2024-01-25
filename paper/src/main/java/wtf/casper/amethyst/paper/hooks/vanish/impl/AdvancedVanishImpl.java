package wtf.casper.amethyst.paper.hooks.vanish.impl;

import com.google.auto.service.AutoService;
import me.quantiom.advancedvanish.util.AdvancedVanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.vanish.IVanish;

@AutoService(IVanish.class)
public class AdvancedVanishImpl implements IVanish {

    @Override
    public boolean isVanished(Player player) {
        return AdvancedVanishAPI.INSTANCE.isPlayerVanished(player);
    }

    @Override
    public void setVanished(Player player, boolean vanished) {
        if (vanished) {
            AdvancedVanishAPI.INSTANCE.vanishPlayer(player, false);
        } else {
            AdvancedVanishAPI.INSTANCE.unVanishPlayer(player, false);
        }
    }

    @Override
    public long getVanishPriority(Player player) {
        return 1;
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("AdvancedVanish");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
