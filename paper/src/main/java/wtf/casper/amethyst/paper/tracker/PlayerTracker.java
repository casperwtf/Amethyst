package wtf.casper.amethyst.paper.tracker;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.events.AsyncPlayerMoveEvent;
import wtf.casper.amethyst.paper.utils.WorldUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTracker implements Runnable {

    @Getter
    private static final Map<UUID, Location> cache = new HashMap<>();

    @Override
    public void run() {
        if (AsyncPlayerMoveEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation(); // get location is not a reference & is a new object apparently
            if (!cache.containsKey(player.getUniqueId())) {
                cache.put(player.getUniqueId(), location);
                continue;
            }

            Location lastLocation = cache.get(player.getUniqueId());

            if (WorldUtils.locationSoftEquals(location, lastLocation, false, true)) {
                continue;
            }

            // this returns true if the event is not cancelled, to avoid declaring a variable for the event we can just use the return value
            if (!new AsyncPlayerMoveEvent(player, true, location, lastLocation).callEvent()) {
                player.teleport(lastLocation);
            } else {
                cache.put(player.getUniqueId(), location);
            }
        }
    }
}
