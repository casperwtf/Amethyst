package wtf.casper.amethyst.paper.schematics;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockContainer {

    private static final Pattern ROTATION_PATTERN = Pattern.compile("rotation=(\\d+)[,\\]]");

    private final Map<Integer, String> palette = new HashMap<>();
    private final Map<Integer, BlockData> cachedBlockData = new HashMap<>();

    private final Map<BlockEntityType, List<Object>> blockEntities = new HashMap<>();

    private final Schematic schematic;
    private final int[] data;

    public BlockContainer(Schematic schematic, NBTCompound tag) {
        this.schematic = schematic;
        this.data = convertBytesToInts(tag.getByteArray("BlockData"));

        NBTCompound palette = tag.getCompound("Palette");
        for (String key : palette.getKeys()) {
            this.palette.put(palette.getInteger(key), key);
        }

        for (ReadWriteNBT compoundTag : tag.getCompoundList("BlockEntities")) {
            if (!compoundTag.getString("Id").contains("sign")) {
                continue;
            }

            if (!blockEntities.containsKey(BlockEntityType.SIGN)) {
                blockEntities.put(BlockEntityType.SIGN, new ArrayList<>());
            }

            int[] positionPosition = compoundTag.getIntArray("Pos");
            int x = positionPosition[0];
            int y = positionPosition[1];
            int z = positionPosition[2];

            int paletteID = getPaletteID(x, y, z);
            String rawData = getRawData(paletteID);
            if (rawData == null) {
                continue;
            }

            Matcher matcher = ROTATION_PATTERN.matcher(rawData);
            int rotation = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;

            SignData signData = new SignData(x, y, z, rotation, compoundTag);
            blockEntities.get(BlockEntityType.SIGN).add(signData);
        }

    }

    public BlockData getBlock(int x, int y, int z) {
        int paletteID = getPaletteID(x, y, z);

        BlockData potentiallyCachedBlockData = cachedBlockData.get(paletteID);
        if (potentiallyCachedBlockData != null) {
            return potentiallyCachedBlockData.clone();
        }

        String rawData = getRawData(paletteID);
        if (rawData == null) {
            return null;
        }

        BlockData blockData = Bukkit.createBlockData(rawData);
        cachedBlockData.put(paletteID, blockData);

        return null;
    }

    // https://github.com/IntellectualSites/FastAsyncWorldEdit/blob/ae949d607b5d2c88f8e12435e1aeb69795e080ea/worldedit-core/src/main/java/com/sk89q/worldedit/extent/clipboard/io/SpongeSchematicReader.java

    /**
     * There's a byte array of block data values that link to a palette, but the palette uses
     * integer values as there thousands of different unique blocks that need to be stored
     * If the palette has over 128 values, this has to be used to prevent corrupted builds from showing up
     *
     * @param rawData The raw byte array of block data
     * @return The updated / converted / fixed integer array
     */
    private int[] convertBytesToInts(byte[] rawData) {
        // Converts bytes to ints
        int[] newDataToUse = new int[rawData.length];

        int index = 0;
        int i = 0;

        int value;
        int varIntLength;

        while (i < rawData.length) {
            value = 0;
            varIntLength = 0;

            while (true) {
                value |= (rawData[i] & 127) << (varIntLength++ * 7);
                if (varIntLength > 5) {
                    throw new RuntimeException("VarInt too big (schematic name = " + schematic.getFileName() + ")");
                }

                if ((rawData[i] & 128) != 128) {
                    i++;
                    break;
                }

                i++;
            }

            newDataToUse[index] = value;

            index++;
        }

        return newDataToUse;
    }

    private int getPaletteID(int x, int y, int z) {
        return data[x + z * schematic.getWidth() + y * schematic.getWidth() * schematic.getLength()];
    }

    private String getRawData(int paletteID) {
        return palette.get(paletteID);
    }
}

