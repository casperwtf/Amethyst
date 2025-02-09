package wtf.casper.amethyst.paper.hooks.combat.impl;

import com.google.auto.service.AutoService;
import nl.marido.deluxecombat.DeluxeCombat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.combat.ICombat;

import java.util.Optional;

@AutoService(ICombat.class)
public class CombatDeluxeCombatHook implements ICombat {

    @Override
    public Optional<Player> getAttacker(Player player) {
        return Optional.ofNullable(DeluxeCombat.getAPI().getCurrentOpponent(player));
    }

    @Override
    public boolean canEnable() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("DeluxeCombat");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public int priority() {
        return 1;
    }
}
