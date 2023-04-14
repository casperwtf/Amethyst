package wtf.casper.amethyst.paper.distributedworkload;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.core.distributedworkload.ScheduledWorkload;

import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PlayerWorkload implements ScheduledWorkload {

    private final UUID playerUUID;
    private final Consumer<Player> action;
    private boolean playerOffline = false;

    @Override
    public void compute() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            playerOffline = true;
            return;
        }

        action.accept(player);
    }

    @Override
    public boolean shouldBeRescheduled() {
        return !playerOffline;
    }
}
