package wtf.casper.amethyst.paper.events.vault.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayerPermissionRemoveEvent extends VaultPermissionEvent {

    private String world;
    private final OfflinePlayer player;
    private final String group;
    private final boolean success;
}
