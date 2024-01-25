package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

@Getter
@AllArgsConstructor
public class BankCreateEvent extends VaultEconomyEvent {

    private final String bankName;
    private final OfflinePlayer player;
    private final EconomyResponse response;
}
