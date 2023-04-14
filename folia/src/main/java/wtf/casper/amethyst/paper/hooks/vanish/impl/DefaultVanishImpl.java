package wtf.casper.amethyst.paper.hooks.vanish.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import wtf.casper.amethyst.paper.AmethystFolia;
import wtf.casper.amethyst.paper.hooks.vanish.IVanish;

public class DefaultVanishImpl implements IVanish {

    @Override
    public boolean isVanished(Player player) {
        return player.hasMetadata("vanished");
    }

    @Override
    public void setVanished(Player player, boolean vanished) {

        if (vanished) {
            player.removeMetadata("vanished", AmethystFolia.getInstance());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(AmethystFolia.getInstance(), player);
            }
        } else {
            player.setMetadata("vanished", new FixedMetadataValue(AmethystFolia.getInstance(), true));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(AmethystFolia.getInstance(), player);
            }
        }
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
