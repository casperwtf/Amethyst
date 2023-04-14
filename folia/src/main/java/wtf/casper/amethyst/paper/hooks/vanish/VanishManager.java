package wtf.casper.amethyst.paper.hooks.vanish;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.vanish.impl.AdvancedVanishImpl;
import wtf.casper.amethyst.paper.hooks.vanish.impl.DefaultVanishImpl;

import java.util.ArrayList;
import java.util.List;

public class VanishManager {

    @Getter
    private static final List<IVanish> vanishes;

    static {
        vanishes = new ArrayList<>();
        for (IVanish iVanish : List.of(new AdvancedVanishImpl(), new DefaultVanishImpl())) {
            if (iVanish.canEnable()) {
                iVanish.enable();
                vanishes.add(iVanish);
            }
        }
    }

    public static void disable() {
        for (IVanish iVanish : vanishes) {
            iVanish.disable();
        }
    }

    /**
     * This method is used to check if a player is vanished.
     * It will return true if the player is vanished, false if not.
     *
     * @return true if the player is vanished
     */
    public static boolean isVanished(Player player) {
        for (IVanish vanish : vanishes) {
            if (vanish.isVanished(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to set a player's vanish state.
     */
    public static void setVanished(Player player, boolean vanished) {
        for (IVanish vanish : vanishes) {
            vanish.setVanished(player, vanished);
        }
    }

    /**
     * This method is used to get all unvanished players.
     *
     * @return a list of all unvanished players
     */
    public static List<Player> getUnvanishedPlayers() {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isVanished(player)) {
                players.add(player);
            }
        }

        return players;
    }

    /*
     * This method is used to get all vanished players.
     * @return a list of all vanished players
     */
    public static List<Player> getVanishedPlayers() {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isVanished(player)) {
                players.add(player);
            }
        }

        return players;
    }
}
