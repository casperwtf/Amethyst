package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Getter
public class PlayerAccountCreateFailedEvent extends VaultEconomyEvent {

    private final OfflinePlayer player;
    private String world;

    public PlayerAccountCreateFailedEvent(OfflinePlayer player, String world) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.world = world;
    }

    public PlayerAccountCreateFailedEvent(OfflinePlayer player) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
    }
}
