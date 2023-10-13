package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

@Getter @RequiredArgsConstructor @AllArgsConstructor
public class PlayerAccountCreateFailedEvent extends VaultEconomyEvent {

    private final OfflinePlayer player;
    private String world;
}
