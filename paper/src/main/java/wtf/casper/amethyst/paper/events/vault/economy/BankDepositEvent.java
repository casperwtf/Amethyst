package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;

@Getter @RequiredArgsConstructor @AllArgsConstructor
public class BankDepositEvent extends VaultEconomyEvent {

    private final String accountName;
    private final double amount;
    private final EconomyResponse response;
}
