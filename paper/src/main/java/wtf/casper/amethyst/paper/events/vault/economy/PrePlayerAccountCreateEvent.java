package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@Getter
public class PrePlayerAccountCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final OfflinePlayer offlinePlayer;
    private String worldName;
    private boolean cancelled = false;

    public PrePlayerAccountCreateEvent(OfflinePlayer offlinePlayer, String worldName) {
        super(!Bukkit.isPrimaryThread());
        this.offlinePlayer = offlinePlayer;
        this.worldName = worldName;
    }

    public PrePlayerAccountCreateEvent(OfflinePlayer offlinePlayer) {
        super(!Bukkit.isPrimaryThread());
        this.offlinePlayer = offlinePlayer;
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
