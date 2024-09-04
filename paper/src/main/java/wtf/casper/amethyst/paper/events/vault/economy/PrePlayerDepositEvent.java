package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@Getter
public class PrePlayerDepositEvent extends VaultEconomyEvent implements Cancellable {

    private final OfflinePlayer player;
    private final double amount;
    private String worldName;
    private boolean cancelled = false;

    public PrePlayerDepositEvent(OfflinePlayer player, double amount, String worldName) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.amount = amount;
        this.worldName = worldName;
    }

    public PrePlayerDepositEvent(OfflinePlayer player, double amount) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
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
