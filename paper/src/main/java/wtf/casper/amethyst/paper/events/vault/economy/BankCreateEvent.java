package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@Getter
public class BankCreateEvent extends VaultEconomyEvent {

    private final String bankName;
    private final OfflinePlayer player;
    private final EconomyResponse response;

    public BankCreateEvent(String bankName, OfflinePlayer player, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.bankName = bankName;
        this.player = player;
        this.response = response;
    }
}
