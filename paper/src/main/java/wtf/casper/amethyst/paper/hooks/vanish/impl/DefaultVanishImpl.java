package wtf.casper.amethyst.paper.hooks.vanish.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.hooks.vanish.IVanish;

public class DefaultVanishImpl implements IVanish {

    @Override
    public boolean isVanished(Player player) {
        return player.hasMetadata("vanished");
    }

    @Override
    public void setVanished(Player player, boolean vanished) {

        if (vanished) {
            player.removeMetadata("vanished", AmethystPaper.getInstance());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(AmethystPaper.getInstance(), player);
            }
        } else {
            player.setMetadata("vanished", new FixedMetadataValue(AmethystPaper.getInstance(), true));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(AmethystPaper.getInstance(), player);
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
