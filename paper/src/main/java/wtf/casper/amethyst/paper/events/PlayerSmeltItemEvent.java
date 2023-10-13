package wtf.casper.amethyst.paper.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class PlayerSmeltItemEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer player;
    private final Block furnace;
    private final ItemStack source;
    private ItemStack result;

    public PlayerSmeltItemEvent(@NotNull OfflinePlayer player, @NotNull final Block furnace, @NotNull final ItemStack source, @NotNull final ItemStack result) {
        super(false);
        this.player = player;
        this.furnace = furnace;
        this.source = source;
        this.result = result;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
