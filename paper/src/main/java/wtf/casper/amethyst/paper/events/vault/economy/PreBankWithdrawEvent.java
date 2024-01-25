package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PreBankWithdrawEvent extends VaultEconomyEvent implements Cancellable {

    private final String accountName;
    private final double amount;
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
