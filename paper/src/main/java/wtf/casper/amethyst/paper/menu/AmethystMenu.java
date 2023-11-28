package wtf.casper.amethyst.paper.menu;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.core.utils.MathUtils;
import wtf.casper.amethyst.paper.utils.GeyserUtils;
import wtf.casper.amethyst.paper.utils.ItemConfigUtils;
import wtf.casper.amethyst.paper.utils.PlaceholderReplacer;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.SuppliedItem;
import xyz.xenondevs.invui.window.Window;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Setter
public abstract class AmethystMenu {


    private final Window window;
    private final Gui topGui;
    @Nullable
    private final Gui bottomGui;

    private Player target;
    public AmethystMenu(Player target, Window window, Gui topGui, @Nullable Gui bottomGui) {
        this.target = target;
        this.window = window;
        this.topGui = topGui;
        this.bottomGui = bottomGui;

        window.addCloseHandler(this::close);
    }

    public CompletableFuture<Void> asyncPreload() {
        return CompletableFuture.completedFuture(null);
    }

    public abstract void init();

    public abstract void close();

    protected void setTitle(String title) {
        window.changeTitle(title);
    }

    public void open() {
        CompletableFuture<Void> asyncPreload = asyncPreload();
        if (asyncPreload.isDone()) {
            init();
            window.open();
        } else {
            asyncPreload.thenRun(() -> {
                init();
                window.open();
            });
        }
    }

//    protected int calcPageStart(int page, int slotsPerPage) {
//        return (page - 1) * slotsPerPage;
//    }
//
//    protected int calcPageEnd(int page, int slotsPerPage, int totalIndexes) {
//        return Math.min(calcPageStart(page, slotsPerPage) + slotsPerPage, totalIndexes);
//    }

    protected void placeItemTop(Section section,
                             @Nullable PlaceholderReplacer replacer,
                             @Nullable OfflinePlayer player,
                             @Nullable Function<Click, Boolean> consumer) {

        final List<Integer> slots = new ArrayList<>();
        scanForSlots(section, "slots", slots);
        scanForSlots(section, "slot", slots);

        if (slots.isEmpty()) {
            return;
        }

        SuppliedItem item = new SuppliedItem(() -> new ItemWrapper(ItemConfigUtils.getItem(section, player, replacer)), consumer);

        for (Integer slot : slots) {
            topGui.setItem(slot, item);
        }
    }

    protected void placeItemBottom(Section section,
                                @Nullable PlaceholderReplacer replacer,
                                @Nullable OfflinePlayer player,
                                @Nullable Function<Click, Boolean> consumer) {

        if (bottomGui == null) {
            return;
        }

        final List<Integer> slots = new ArrayList<>();
        scanForSlots(section, "slots", slots);
        scanForSlots(section, "slot", slots);

        if (slots.isEmpty()) {
            return;
        }

        SuppliedItem item = new SuppliedItem(() -> new ItemWrapper(ItemConfigUtils.getItem(section, player, replacer)), consumer);

        for (Integer slot : slots) {
            bottomGui.setItem(slot, item);
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
