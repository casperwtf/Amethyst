package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PreBankCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final String bankName;
    private final OfflinePlayer player;

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
