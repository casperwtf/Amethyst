package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

@Getter
public class PreBankWithdrawEvent extends VaultEconomyEvent implements Cancellable {

    private final String accountName;
    private final double amount;
    private boolean cancelled = false;

    public PreBankWithdrawEvent(String accountName, double amount) {
        super(!Bukkit.isPrimaryThread());
        this.accountName = accountName;
        this.amount = amount;
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
