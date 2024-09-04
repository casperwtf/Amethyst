package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

@Getter
public class BankAccountCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final String bankName;
    private final String account;
    private final EconomyResponse response;

    private boolean cancelled;

    public BankAccountCreateEvent(String bankName, String account, EconomyResponse response) {
        super(!Bukkit.isPrimaryThread());
        this.bankName = bankName;
        this.account = account;
        this.response = response;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
