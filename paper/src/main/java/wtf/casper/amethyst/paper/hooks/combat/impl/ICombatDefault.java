package wtf.casper.amethyst.paper.hooks.combat.impl;

import com.google.auto.service.AutoService;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.combat.ICombat;

import java.util.Optional;

@AutoService(ICombat.class)
public class ICombatDefault implements ICombat {

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

    @Override
    public Optional<Player> getAttacker(Player player) {
        return Optional.empty();
    }
}
