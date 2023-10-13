package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

@RequiredArgsConstructor @AllArgsConstructor @Getter
public class PrePlayerAccountCreateEvent extends VaultEconomyEvent implements Cancellable {

    private final OfflinePlayer offlinePlayer;
    private String worldName;
    private boolean cancelled;

    public PrePlayerAccountCreateEvent(OfflinePlayer offlinePlayer, String worldName) {
        this.offlinePlayer = offlinePlayer;
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
