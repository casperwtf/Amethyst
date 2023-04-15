package wtf.casper.amethyst.paper.hooks.combat;

import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.IHook;

public interface ICombat extends IHook {

    Player getAttacker(Player player);

}
