package wtf.casper.amethyst.paper.events.vault.permissions;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class VaultPermissionEvent extends Event {
    private final static HandlerList handlers = new HandlerList();

    public VaultPermissionEvent() {
        super(true);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
