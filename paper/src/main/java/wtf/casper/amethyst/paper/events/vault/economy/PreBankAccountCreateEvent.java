package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

@Getter
public class PreBankAccountCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final String bankName;
    private final String account;

    private boolean cancelled;

    public PreBankAccountCreateEvent(String bankName, String account) {
        super(!Bukkit.isPrimaryThread());
        this.bankName = bankName;
        this.account = account;
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
