/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Chubbyduck1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package wtf.casper.amethyst.paper.hologrambridge.connector.impl;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.HologramData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.paper.hologrambridge.connector.Connector;
import wtf.casper.amethyst.paper.hologrambridge.hologram.Hologram;
import wtf.casper.amethyst.paper.hologrambridge.hologram.VisibilityManager;
import wtf.casper.amethyst.paper.hologrambridge.hologram.impl.AmethystHologram;
import wtf.casper.amethyst.paper.hologrambridge.lines.Line;
import wtf.casper.amethyst.paper.hologrambridge.lines.types.TextLine;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FancyHologramImpl implements Connector {

    @Override
    public Hologram createHologram(final Location location) {
        HologramData data = new HologramData("holobridge-" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        Location clone = location.clone();
        data.setLocation(clone);

        de.oliver.fancyholograms.api.Hologram hologram = FancyHolograms.get().getHologramsManager().create(data);

        hologram.createHologram();
        hologram.showHologram(Bukkit.getOnlinePlayers());
        FancyHolograms.get().getHologramsManager().addHologram(hologram);

        return new AmethystHologram(this, hologram, clone);
    }

    @Override
    public void setLine(final Hologram hologram,
                        final int lineIndex,
                        final Line line) {
        if (!(line instanceof TextLine)) {
            return;
        }

        // this is how the plugin does it, kind of weird
        // TODO: optimize

        de.oliver.fancyholograms.api.Hologram from = from(hologram);
        List<String> lines = from.getData().getText();
        String text = ((TextLine) line).getText();

        if (lineIndex >= lines.size()) {
            lines.add(text == null ? " " : text);
        } else if (text == null) {
            lines.remove(lineIndex);
        } else {
            lines.set(lineIndex, text);
        }

        from.getData().setText(lines);
        from.updateHologram();
    }

    @Override
    public void updateLine(final Hologram hologram,
                           final int lineIndex,
                           final Line line) {
        setLine(hologram, lineIndex, line);
    }

    @Override
    public void appendLine(final Hologram hologram, final Line line) {
        if (!(line instanceof TextLine)) {
            return;
        }

        de.oliver.fancyholograms.api.Hologram from = from(hologram);
        List<String> lines = from.getData().getText();
        String text = ((TextLine) line).getText();

        lines.add(text == null ? " " : text);

        from.getData().setText(lines);
        from.updateHologram();
    }

    @Override
    public void clearLines(final Hologram hologram) {
        de.oliver.fancyholograms.api.Hologram from = from(hologram);
        from.getData().setText(Collections.emptyList());
        from.updateHologram();
    }

    @Override
    public void teleport(final Hologram hologram, final Location location) {
        de.oliver.fancyholograms.api.Hologram from = from(hologram);
        from.getData().setLocation(location);
        from.updateHologram();
    }

    @Override
    public void delete(final Hologram hologram) {
        de.oliver.fancyholograms.api.Hologram from = from(hologram);
        from.deleteHologram();
    }

    @Override
    public void setVisibleByDefault(final VisibilityManager visibilityManager, final boolean visibleByDefault) {
        // no-op? doesnt seem like its supported
    }

    @Override
    public void showTo(final VisibilityManager visibilityManager, final Player player) {
        de.oliver.fancyholograms.api.Hologram from = from(visibilityManager.getHologram());
        from.showHologram(player);
    }

    @Override
    public void hideTo(final VisibilityManager visibilityManager, final Player player) {
        de.oliver.fancyholograms.api.Hologram from = from(visibilityManager.getHologram());
        from.hideHologram(player);
    }

    private de.oliver.fancyholograms.api.Hologram from(Hologram hologram) {
        return (de.oliver.fancyholograms.api.Hologram) hologram.getHologramAsObject();
    }
}