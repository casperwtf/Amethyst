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

package wtf.casper.amethyst.paper.hologrambridge.hologram.impl;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.paper.hologrambridge.connector.Connector;
import wtf.casper.amethyst.paper.hologrambridge.hologram.Hologram;
import wtf.casper.amethyst.paper.hologrambridge.hologram.VisibilityManager;
import wtf.casper.amethyst.paper.hologrambridge.lines.Line;
import wtf.casper.amethyst.paper.hologrambridge.lines.impl.AmethystItemLine;
import wtf.casper.amethyst.paper.hologrambridge.lines.impl.AmethystTextLine;
import wtf.casper.amethyst.paper.hologrambridge.lines.types.ItemLine;
import wtf.casper.amethyst.paper.hologrambridge.lines.types.TextLine;

import java.util.ArrayList;
import java.util.List;

/**
 * The default implementation of the {@link Hologram}
 */
public final class AmethystHologram implements Hologram {

    private final Connector connector;
    private final Object hologramObject;
    private final VisibilityManager visibilityManager;
    private final List<Line> lines = new ArrayList<>();
    private Location location;

    /**
     * Create a new hologram
     *
     * @param connector      The connector to use
     * @param hologramObject The hologram object for the connector
     * @param location       The starting location
     */
    public AmethystHologram(final Connector connector,
                            final Object hologramObject,
                            final Location location) {
        this.connector = connector;
        this.hologramObject = hologramObject;
        this.location = location;

        this.visibilityManager = new AmethystVisibilityManager(this, this.connector);
    }

    @Override
    public List<Line> getLines() {
        return this.lines;
    }

    @Override
    public void clearLines() {
        this.lines.clear();
        this.connector.clearLines(this);
    }

    @Override
    public Line getLineAt(final int index) {
        return this.lines.get(index);
    }

    @Override
    public int getLineIndex(final Line line) {
        return this.lines.indexOf(line);
    }

    @Override
    public void setLineAt(final int index, final Line line) {
        this.lines.set(index, line);
        this.connector.setLine(this, index, line);
    }

    @Override
    public void updateLine(final int index, final Line line) {
        this.connector.updateLine(this, index, line);
    }

    @Override
    public void appendLine(final Line line) {
        this.lines.add(line);
        this.connector.appendLine(this, line);
    }

    @Override
    public ItemLine appendItemLine(final ItemStack itemStack) {
        final ItemLine itemLine = new AmethystItemLine(this, itemStack);
        this.appendLine(itemLine);

        return itemLine;
    }

    @Override
    public TextLine appendTextLine(final String text) {
        final TextLine textLine = new AmethystTextLine(this, text);
        this.appendLine(textLine);

        return textLine;
    }

    @Override
    public VisibilityManager getVisibilityManager() {
        return this.visibilityManager;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void teleport(final Location location) {
        this.location = location;
        this.connector.teleport(this, location);
    }

    @Override
    public void delete() {
        this.connector.delete(this);
    }

    @Override
    public int size() {
        return this.lines.size();
    }

    @Override
    public Object getHologramAsObject() {
        return this.hologramObject;
    }

}
