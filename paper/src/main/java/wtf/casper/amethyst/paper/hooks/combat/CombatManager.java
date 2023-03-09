package wtf.casper.amethyst.paper.hooks.combat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.hooks.combat.impl.ICombatDefault;
import wtf.casper.amethyst.paper.hooks.combat.impl.ICombatDeluxeCombat;

import java.util.ArrayList;
import java.util.List;

public class CombatManager {

    private static final List<ICombat> combatHandlers;

    static {
        combatHandlers = new ArrayList<>();
        registerCombatHandler(new ICombatDefault());
        registerCombatHandler(new ICombatDeluxeCombat());
    }

    public static void disable() {
        for (ICombat iCombat : combatHandlers) {
            iCombat.disable();
        }
    }

    public static void registerCombatHandler(ICombat combatHandler) {
        if (combatHandler.canEnable()) {
            combatHandlers.add(combatHandler);
            combatHandler.enable();
        }
    }

    @Nullable
    public static Player getAttacker(Player player) {
        for (ICombat combatHandler : combatHandlers) {
            Player attacker = combatHandler.getAttacker(player);
            if (attacker != null) {
                return attacker;
            }
        }
        return null;
    }

    public static boolean isInCombat(Player player) {
        return getAttacker(player) != null;
    }
}
