package wtf.casper.amethyst.paper.schematics;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-3.md
 */
@Getter
public class Schematic {

    private final static BlockData AIR = Material.AIR.createBlockData();

    private final String fileName;
    private final int version;
    private final int dataVersion;
    private final int length;
    private final int width;
    private final int height;
    private final int[] offset;
    private final int[] worldEditCopyOffset;
    private final BlockContainer blocks;
    //TODO: Implement these fields to meet the spec
    // private final BiomeContainer biomes;
    // private final EntityObject[] entities;

    /**
     * Load a schematic from a file
     *
     * @param file The schematic file to load, must be in the Sponge Schematic format, extension does not matter
     */
    public Schematic(File file) {
        this.fileName = file.getName();
        NBTCompound schematic;
        try {
            schematic = NBTFile.readFrom(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (schematic.hasTag("Version")) {
            this.version = schematic.getInteger("Version");
        } else {
            this.version = 0;
        }

        if (version < 2) { // we only support version 3, if a ver 4 ever releases itll probably have backwards compatibility
            throw new IllegalArgumentException("Unsupported schematic version: " + version);
        }

        this.dataVersion = schematic.getInteger("DataVersion");

        this.length = schematic.getInteger("Length");
        this.width = schematic.getInteger("Width");
        this.height = schematic.getInteger("Height");
        this.offset = schematic.getIntArray("Offset");

        NBTCompound metadata = schematic.getCompound("Metadata");
        if (metadata == null) {
            throw new IllegalArgumentException("Schematic is missing metadata");
        }
        int copyOffsetX = metadata.getInteger("WEOffsetX");
        int copyOffsetY = metadata.getInteger("WEOffsetY");
        int copyOffsetZ = metadata.getInteger("WEOffsetZ");
        this.worldEditCopyOffset = new int[]{
                copyOffsetX,
                copyOffsetY,
                copyOffsetZ
        };

        this.blocks = new BlockContainer(this, schematic);
    }


    /**
     * Paste the schematic at the given location
     *
     * @param location           The location to paste the schematic at
     * @param useWorldEditOffset If the world edit offset should be applied to the location
     * @param blockDataConsumer  The consumer to apply the block data to the block. This is up to the user to handle.
     */
    public void paste(Location location, boolean useWorldEditOffset, BiConsumer<Block, BlockData> blockDataConsumer) {
        location = location.clone();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    BlockData sourceBlockData = blocks.getBlock(x, y, z);
                    if (sourceBlockData == null) {
                        sourceBlockData = AIR;
                    }

                    Vector placeOffset = new Vector(x, y, z);
                    Location targetLocation = location.clone().add(placeOffset);
                    if (useWorldEditOffset) {
                        Vector worldEditOffset = new Vector(worldEditCopyOffset[0], worldEditCopyOffset[1], worldEditCopyOffset[2]);
                        targetLocation.add(worldEditOffset);
                    }
                    Block block = targetLocation.getBlock();

                    blockDataConsumer.accept(block, sourceBlockData);
                }
            }
        }
    }
}