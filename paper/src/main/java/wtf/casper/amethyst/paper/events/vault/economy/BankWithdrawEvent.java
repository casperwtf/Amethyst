package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

@Getter
public class BankWithdrawEvent extends VaultEconomyEvent {

    private final String accountName;
    private final double amount;
    private final EconomyResponse response;

    public BankWithdrawEvent(String accountName, double amount, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.accountName = accountName;
        this.amount = amount;
        this.response = response;
    }
}
