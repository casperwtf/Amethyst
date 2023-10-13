package wtf.casper.amethyst.paper.tracker;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.events.AsyncPlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTracker implements Runnable {

    @Getter
    private static final Map<UUID, Location> cache = new HashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!cache.containsKey(player.getUniqueId())) {
                cache.put(player.getUniqueId(), player.getLocation());
            }

            Location lastLocation = cache.get(player.getUniqueId());

            if (cache.get(player.getUniqueId()).equals(player.getLocation())) {
                continue;
            }

            boolean isCancelled = new AsyncPlayerMoveEvent(player, player.getLocation(), lastLocation).callEvent();
            if (!isCancelled) {
                player.teleport(lastLocation);
            } else {
                cache.put(player.getUniqueId(), player.getLocation());
            }
        }
    }
}
