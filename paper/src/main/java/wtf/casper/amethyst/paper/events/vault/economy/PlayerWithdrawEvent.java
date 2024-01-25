package wtf.casper.amethyst.paper.events.vault.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PlayerWithdrawEvent extends VaultEconomyEvent {

    private final OfflinePlayer player;
    private final double amount;
    private String worldName;
    private final EconomyResponse response;
}
