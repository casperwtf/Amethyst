package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@Getter
public class PreBankCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final String bankName;
    private final OfflinePlayer player;

    private boolean cancelled;

    public PreBankCreateEvent(String bankName, OfflinePlayer player) {
        super(!Bukkit.isPrimaryThread());
        this.bankName = bankName;
        this.player = player;
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
