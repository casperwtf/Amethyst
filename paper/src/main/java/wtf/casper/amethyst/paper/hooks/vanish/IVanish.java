package wtf.casper.amethyst.paper.hooks.vanish;

import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.IHook;

public interface IVanish extends IHook {
    boolean isVanished(Player player);

    void setVanished(Player player, boolean vanished);

    long getVanishPriority(Player player);
}
