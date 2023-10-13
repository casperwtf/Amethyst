package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

@Getter @RequiredArgsConstructor @AllArgsConstructor
public class BankCreateEvent extends VaultEconomyEvent {

    private final String bankName;
    private final OfflinePlayer player;
    private final EconomyResponse response;
}
