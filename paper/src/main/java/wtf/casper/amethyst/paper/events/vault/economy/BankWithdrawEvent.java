package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;

@Getter
@AllArgsConstructor
public class BankWithdrawEvent extends VaultEconomyEvent {

    private final String accountName;
    private final double amount;
    private final EconomyResponse response;
}
