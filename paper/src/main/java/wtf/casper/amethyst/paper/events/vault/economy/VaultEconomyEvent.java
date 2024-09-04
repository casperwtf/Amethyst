package wtf.casper.amethyst.paper.events.vault.economy;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class VaultEconomyEvent extends Event {

    public VaultEconomyEvent(boolean async) {
        super(async);
    }

    private final static HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
