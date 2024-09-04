package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Getter
public class PlayerWithdrawEvent extends VaultEconomyEvent {

    private final OfflinePlayer player;
    private final double amount;
    private String worldName;
    private final EconomyResponse response;

    public PlayerWithdrawEvent(OfflinePlayer player, double amount, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.amount = amount;
        this.response = response;
    }

    public PlayerWithdrawEvent(OfflinePlayer player, double amount, String worldName, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.amount = amount;
        this.worldName = worldName;
        this.response = response;
    }
}
