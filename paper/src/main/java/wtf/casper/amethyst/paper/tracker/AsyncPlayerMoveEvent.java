package wtf.casper.amethyst.paper.tracker;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerMoveEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final Location to;
    @Getter private final Location from;
    private boolean cancelled = false;

    public AsyncPlayerMoveEvent(@NotNull Player who, Location to, Location from) {
        super(who);
        this.to = to;
        this.from = from;
    }

    public AsyncPlayerMoveEvent(@NotNull Player who, boolean async, Location to, Location from) {
        super(who, async);
        this.to = to;
        this.from = from;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public boolean hasMoved() {
        return !to.equals(from);
    }

    public boolean hasChangedWorld() {
        return !to.getWorld().equals(from.getWorld());
    }

    public boolean hasChangedBlock() {
        return to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ();
    }

    public boolean hasChangedLocation() {
        return to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ();
    }

    public boolean hasChangedDirection() {
        return to.getYaw() != from.getYaw() || to.getPitch() != from.getPitch();
    }

    public boolean hasChangedPosition() {
        return hasChangedLocation() || hasChangedDirection() || hasChangedWorld();
    }
}
