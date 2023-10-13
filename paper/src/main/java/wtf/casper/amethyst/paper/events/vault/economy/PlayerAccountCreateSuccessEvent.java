package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

@Getter @RequiredArgsConstructor @AllArgsConstructor
public class PlayerAccountCreateSuccessEvent extends VaultEconomyEvent {

    private final OfflinePlayer player;
    private String world;
    private final static HandlerList handlers = new HandlerList();
}
