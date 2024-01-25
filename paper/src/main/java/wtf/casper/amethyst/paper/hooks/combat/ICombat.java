package wtf.casper.amethyst.paper.hooks.combat;

import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.IHook;

import java.util.Optional;

public interface ICombat extends IHook {

    Optional<Player> getAttacker(Player player);

}
