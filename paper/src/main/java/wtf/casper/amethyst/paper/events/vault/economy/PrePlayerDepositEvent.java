package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PrePlayerDepositEvent extends VaultEconomyEvent implements Cancellable {

    private final OfflinePlayer player;
    private final double amount;
    private String worldName;
    private boolean cancelled;

    public PrePlayerDepositEvent(OfflinePlayer player, double amount, String worldName) {
        this.player = player;
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
