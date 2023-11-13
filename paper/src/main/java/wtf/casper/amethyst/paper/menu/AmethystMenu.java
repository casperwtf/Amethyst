package wtf.casper.amethyst.paper.menu;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.MathUtils;
import wtf.casper.amethyst.paper.ryseinventory.animator.SlideAnimation;
import wtf.casper.amethyst.paper.ryseinventory.content.IntelligentItem;
import wtf.casper.amethyst.paper.ryseinventory.content.InventoryContents;
import wtf.casper.amethyst.paper.ryseinventory.content.InventoryProvider;
import wtf.casper.amethyst.paper.ryseinventory.enums.InventoryOpenerType;
import wtf.casper.amethyst.paper.ryseinventory.enums.TimeSetting;
import wtf.casper.amethyst.paper.ryseinventory.pagination.RyseInventory;
import wtf.casper.amethyst.paper.utils.GeyserUtils;
import wtf.casper.amethyst.paper.utils.ItemConfigUtils;
import wtf.casper.amethyst.paper.utils.MenuUtil;
import wtf.casper.amethyst.paper.utils.PlaceholderReplacer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Setter
public abstract class AmethystMenu implements InventoryProvider {

    @Getter @Nullable
    private final FloodgateApi floodgateApi;
    private int slots;
    private String title;
    @Getter
    private RyseInventory inventory;

    public AmethystMenu(int slots, String title) {
        this.floodgateApi = GeyserUtils.floodgate();
        this.slots = slots;
        this.title = title;
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        if (isBedrock(player.getUniqueId()) && getFloodgateApi() != null) {
            updateBedrock(player, contents);
        } else {
            updateJava(player, contents);
        }
    }

    @Override
    public void close(Player player, RyseInventory inventory) {
        if (isBedrock(player.getUniqueId()) && getFloodgateApi() != null) {
            closeBedrock(player, inventory);
        } else {
            closeJava(player, inventory);
        }
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        if (isBedrock(player.getUniqueId()) && getFloodgateApi() != null) {
            initBedrock(player, contents);
        } else {
            initJava(player, contents);
        }
    }

    @Override
    public void anvil(Player player, AnvilGUI.Builder anvil) {
        if (isBedrock(player.getUniqueId()) && getFloodgateApi() != null) {
            anvilBedrock(player, anvil);
        } else {
            anvilJava(player, anvil);
        }
    }


    @Override
    public void init(Player player, InventoryContents contents, SlideAnimation animation) {
        if (isBedrock(player.getUniqueId()) && getFloodgateApi() != null) {
            initBedrock(player, contents, animation);
        } else {
            initJava(player, contents, animation);
        }
    }

    public abstract void updateJava(Player player, InventoryContents contents);

    public abstract void closeJava(Player player, RyseInventory inventory);

    public abstract void initJava(Player player, InventoryContents contents);

    // Override if needed
    public void initJava(Player player, InventoryContents contents, SlideAnimation animation) {
        initJava(player, contents);
    }

    // Override if needed
    public void anvilJava(Player player, AnvilGUI.Builder anvil) {
    }

    // Override if needed
    public void updateBedrock(Player player, InventoryContents contents) {
        updateJava(player, contents);
    }

    // Override if needed
    public void closeBedrock(Player player, RyseInventory inventory) {
        closeJava(player, inventory);
    }

    // Override if needed
    public void initBedrock(Player player, InventoryContents contents) {
        initJava(player, contents);
    }

    // Override if needed
    public void initBedrock(Player player, InventoryContents contents, SlideAnimation animation) {
        initJava(player, contents, animation);
    }

    // Override if needed
    public void anvilBedrock(Player player, AnvilGUI.Builder anvil) {
        anvilJava(player, anvil);
    }

    private boolean isBedrock(UUID player) {
        return GeyserUtils.isUserBedrock(player);
    }


    protected SimpleForm.Builder getGeyserMenu() {
        if (getFloodgateApi() == null) {
            throw new IllegalStateException("Floodgate API is null!");
        }

        return AmethystGeyserMenu.simpleFormBuilder();
    }

    protected void setTitle(String title) {
        if (inventory == null) {
            this.title = title;
            return;
        }

        List<Player> viewers = new ArrayList<>(inventory.getOpenedPlayers().stream().map(uuid -> Bukkit.getServer().getPlayer(uuid)).filter(Objects::nonNull).toList());
        for (Player viewer : viewers) {
            inventory.updateTitle(viewer, title);
        }
        this.title = title;
    }

    @SneakyThrows
    public void open(JavaPlugin plugin, Player... targets) {
        RyseInventory inventory = MenuUtil.getInventory(plugin, this, slots, title);
        this.inventory = inventory;
        for (Player target : targets) {
            inventory.open(target);
        }
    }

    @SneakyThrows
    public void open(JavaPlugin plugin, PlaceholderReplacer replacer, Player... targets) {
        RyseInventory inventory = MenuUtil.getInventory(plugin, this, slots, replacer.parse(title));
        this.inventory = inventory;
        for (Player target : targets) {
            inventory.open(target);
        }
    }

    public void openAnvil(JavaPlugin plugin, Player player, PlaceholderReplacer replacer, String title) {
        RyseInventory ryseInventory = MenuUtil.getInventory(plugin, this, slots, replacer.parse(title), InventoryOpenerType.ANVIL);
        this.inventory = ryseInventory;
        ryseInventory.open(player);
    }

    protected void disableUpdating() {
        if (inventory == null) {
            AmethystLogger.warning("Tried to disable updating on a menu that hasn't been opened yet!");
            return;
        }
        setUpdate(0, TimeSetting.MILLISECONDS);
    }

    protected void setUpdate(int time, TimeSetting timeSetting) {
        if (inventory == null) {
            AmethystLogger.warning("Tried to set update time on a menu that hasn't been opened yet!");
            return;
        }
        inventory.updatePeriod(time, timeSetting);
    }

    protected int calcPageStart(int page, int slotsPerPage) {
        return (page - 1) * slotsPerPage;
    }

    protected int calcPageEnd(int page, int slotsPerPage, int totalIndexes) {
        return Math.min(calcPageStart(page, slotsPerPage) + slotsPerPage, totalIndexes);
    }

    protected void placeItem(Section section,
                             @Nullable PlaceholderReplacer replacer,
                             @Nullable OfflinePlayer player,
                             InventoryContents contents,
                             @Nullable Consumer<InventoryClickEvent> consumer) {

        final List<Integer> slots = new ArrayList<>();
        scanForSlots(section, "slots", slots);
        scanForSlots(section, "slot", slots);

        if (slots.isEmpty()) return;

        for (Integer slot : slots) {
            contents.updateOrSet(slot, consumer == null ?
                    IntelligentItem.empty(ItemConfigUtils.getItem(section, player, replacer)) :
                    IntelligentItem.of(ItemConfigUtils.getItem(section, player, replacer), consumer));
        }

    }

    protected void scanForSlots(Section section, String id, List<Integer> slots) {
        section.getOptionalIntList(id).ifPresent(slots::addAll);
        section.getOptionalInt(id).ifPresent(slots::add);
        section.getOptionalString(id).ifPresent(s -> scanString(s, slots));
        section.getOptionalStringList(id).ifPresent(strings -> strings.forEach(s -> scanString(s, slots)));
    }

    private void scanString(String s, List<Integer> slots) {
        if (MathUtils.validateInt(s)) {
            slots.add(Integer.parseInt(s));
            return;
        }

        String[] split = s.split("-");
        if (split.length == 1 && MathUtils.validateInt(split[0])) {
            slots.add(Integer.parseInt(split[0]));
        } else if (split.length == 2 && MathUtils.validateInt(split[0]) && MathUtils.validateInt(split[1])) {
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            for (int i = start; i <= end; i++) {
                slots.add(i);
            }
        }
    }
}
