package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

@Getter
public class AccountWithdrawEvent extends VaultEconomyEvent {

    private final String account;
    private final double amount;
    private String worldName;
    private final EconomyResponse response;

    public AccountWithdrawEvent(String account, double amount, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.account = account;
        this.amount = amount;
        this.response = response;
    }

    public AccountWithdrawEvent(String account, double amount, String worldName, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.account = account;
        this.amount = amount;
        this.worldName = worldName;
        this.response = response;
    }
}
