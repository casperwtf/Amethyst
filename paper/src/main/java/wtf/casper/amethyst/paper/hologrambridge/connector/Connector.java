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

package wtf.casper.amethyst.paper.hologrambridge.connector;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hologrambridge.hologram.Hologram;
import wtf.casper.amethyst.paper.hologrambridge.hologram.VisibilityManager;
import wtf.casper.amethyst.paper.hologrambridge.lines.Line;

/**
 * The {@link Connector} which handles connections between multiple {@link Hologram} sources
 */
public interface Connector {

    /**
     * Create a {@link Hologram}
     *
     * @param location The starting {@link Location}
     * @return The created {@link Hologram}
     */
    Hologram createHologram(final Location location);

    /**
     * Set a holograms line at an index
     *
     * @param hologram  The parent {@link Hologram}
     * @param lineIndex The {@link Line} index
     * @param line      The {@link Line}
     */
    void setLine(final Hologram hologram, final int lineIndex, final Line line);

    /**
     * Update a {@link Hologram}s line
     *
     * @param hologram  The parent {@link Hologram}
     * @param lineIndex The {@link Line} index
     * @param line      The {@link Line}
     */
    void updateLine(final Hologram hologram, final int lineIndex, final Line line);

    /**
     * Append a {@link Line} to a {@link Hologram}
     *
     * @param hologram The parent {@link Hologram}
     * @param line     The {@link Line}
     */
    void appendLine(final Hologram hologram, final Line line);

    /**
     * Clear the lines of a {@link Hologram}
     *
     * @param hologram The parent {@link Hologram}
     */
    void clearLines(final Hologram hologram);

    /**
     * Teleport a hologram
     *
     * @param hologram The {@link Hologram} to teleport
     * @param location The {@link Location} to teleport it to
     */
    void teleport(final Hologram hologram, final Location location);

    /**
     * Delete a hologram
     */
    void delete(final Hologram hologram);

    /**
     * Update the Hologram to be visible by default
     *
     * @param visibilityManager The {@link VisibilityManager} to use
     * @param visibleByDefault  The default visibility
     */
    void setVisibleByDefault(final VisibilityManager visibilityManager, final boolean visibleByDefault);

    /**
     * Show a hologram to a player
     *
     * @param visibilityManager The {@link VisibilityManager} to use
     * @param player            The player
     */
    void showTo(final VisibilityManager visibilityManager, final Player player);

    /**
     * Hide a hologram from a player
     *
     * @param visibilityManager The {@link VisibilityManager} to use
     * @param player            The player
     */
    void hideTo(final VisibilityManager visibilityManager, final Player player);

}
