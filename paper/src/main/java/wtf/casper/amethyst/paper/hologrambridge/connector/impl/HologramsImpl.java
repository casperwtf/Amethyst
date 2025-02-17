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

import com.sainttx.holograms.api.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.paper.hologrambridge.connector.Connector;
import wtf.casper.amethyst.paper.hologrambridge.hologram.Hologram;
import wtf.casper.amethyst.paper.hologrambridge.hologram.VisibilityManager;
import wtf.casper.amethyst.paper.hologrambridge.hologram.impl.AmethystHologram;
import wtf.casper.amethyst.paper.hologrambridge.lines.Line;
import wtf.casper.amethyst.paper.hologrambridge.lines.types.ItemLine;
import wtf.casper.amethyst.paper.hologrambridge.lines.types.TextLine;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public final class HologramsImpl implements Connector {

    @Override
    public Hologram createHologram(final Location location) {
        return new AmethystHologram(
                this,
                new com.sainttx.holograms.api.Hologram(UUID.randomUUID().toString(), location, false),
                location
        );
    }

    @Override
    public void setLine(final Hologram hologram,
                        final int lineIndex,
                        final Line line) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);

        if (!hologramOptional.isPresent()) {
            return;
        }

        final com.sainttx.holograms.api.Hologram saintHologram = hologramOptional.get();
        saintHologram.removeLine(saintHologram.getLine(lineIndex));

        if (line instanceof ItemLine) {
            saintHologram.addLine(
                    new com.sainttx.holograms.api.line.ItemLine(saintHologram, ((ItemLine) line).getItemStack()),
                    lineIndex
            );
            return;
        }

        if (line instanceof TextLine) {
            saintHologram.addLine(
                    new com.sainttx.holograms.api.line.TextLine(saintHologram, ((TextLine) line).getText()),
                    lineIndex
            );
        }
    }

    @Override
    public void updateLine(final Hologram hologram,
                           final int lineIndex,
                           final Line line) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);

        if (!hologramOptional.isPresent()) {
            return;
        }

        final com.sainttx.holograms.api.Hologram saintHologram = hologramOptional.get();
        final HologramLine hologramLine = saintHologram.getLine(lineIndex);

        if (line instanceof ItemLine
                && hologramLine instanceof com.sainttx.holograms.api.line.ItemLine) {
            final ItemStack itemStack = ((ItemLine) line).getItemStack();
            ((com.sainttx.holograms.api.line.ItemLine) hologramLine).setItem(itemStack);

            return;
        }

        if (line instanceof TextLine
                && hologramLine instanceof com.sainttx.holograms.api.line.TextLine) {
            final String text = ((TextLine) line).getText();
            ((com.sainttx.holograms.api.line.TextLine) hologramLine).setText(text);
        }
    }

    @Override
    public void appendLine(final Hologram hologram,
                           final Line line) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);

        if (!hologramOptional.isPresent()) {
            return;
        }

        final com.sainttx.holograms.api.Hologram saintHologram = hologramOptional.get();

        if (line instanceof ItemLine) {
            saintHologram.addLine(
                    new com.sainttx.holograms.api.line.ItemLine(saintHologram, ((ItemLine) line).getItemStack())
            );
            return;
        }

        if (line instanceof TextLine) {
            saintHologram.addLine(
                    new com.sainttx.holograms.api.line.TextLine(saintHologram, ((TextLine) line).getText())
            );
        }
    }

    @Override
    public void clearLines(final Hologram hologram) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);

        if (!hologramOptional.isPresent()) {
            return;
        }

        final com.sainttx.holograms.api.Hologram saintHologram = hologramOptional.get();
        final Iterator<HologramLine> hologramLineIterator = saintHologram.getLines().iterator();

        while (hologramLineIterator.hasNext()) {
            final HologramLine hologramLine = hologramLineIterator.next();
            saintHologram.removeLine(hologramLine);
        }
    }

    @Override
    public void teleport(final Hologram hologram,
                         final Location location) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);

        if (!hologramOptional.isPresent()) {
            return;
        }

        final com.sainttx.holograms.api.Hologram saintHologram = hologramOptional.get();
        saintHologram.teleport(location);
    }

    @Override
    public void delete(final Hologram hologram) {
        final Optional<com.sainttx.holograms.api.Hologram> hologramOptional = this.getHologram(hologram);
        hologramOptional.ifPresent(com.sainttx.holograms.api.Hologram::despawn);
    }

    @Override
    public void setVisibleByDefault(final VisibilityManager visibilityManager, final boolean visibleByDefault) {
        throw new UnsupportedOperationException("Holograms does not support per-player holograms!");
    }

    @Override
    public void showTo(final VisibilityManager visibilityManager, final Player player) {
        throw new UnsupportedOperationException("Holograms does not support per-player holograms!");
    }

    @Override
    public void hideTo(final VisibilityManager visibilityManager, final Player player) {
        throw new UnsupportedOperationException("Holograms does not support per-player holograms!");
    }

    private Optional<com.sainttx.holograms.api.Hologram> getHologram(final Hologram hologram) {
        final Object hologramObject = hologram.getHologramAsObject();

        if (hologramObject instanceof com.sainttx.holograms.api.Hologram) {
            return Optional.of((com.sainttx.holograms.api.Hologram) hologramObject);
        }

        return Optional.empty();
    }

}
