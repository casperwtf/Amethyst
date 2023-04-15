package wtf.casper.amethyst.paper.hooks.combat.impl;

import nl.marido.deluxecombat.DeluxeCombat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.combat.ICombat;

public class ICombatDeluxeCombat implements ICombat {

    @Override
    public Player getAttacker(Player player) {
        return DeluxeCombat.getAPI().getCurrentOpponent(player);
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
}
