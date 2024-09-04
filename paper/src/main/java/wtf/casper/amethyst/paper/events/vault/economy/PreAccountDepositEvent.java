package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

@Getter
public class PreAccountDepositEvent extends VaultEconomyEvent implements Cancellable {

    private final String account;
    private final double amount;
    private String worldName;
    private boolean cancelled = false;

    public PreAccountDepositEvent(String account, double amount) {
        super(!Bukkit.isPrimaryThread());
        this.account = account;
        this.amount = amount;
    }

    public PreAccountDepositEvent(String account, double amount, String worldName) {
        super(!Bukkit.isPrimaryThread());
        this.account = account;
        this.amount = amount;
        this.worldName = worldName;
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
