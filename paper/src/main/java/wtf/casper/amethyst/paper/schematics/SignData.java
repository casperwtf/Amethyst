package wtf.casper.amethyst.paper.schematics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SignData {
    private final List<String> textLines = new ArrayList<>();
    private final BlockFace rotation;
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;

    public SignData() {
        this(0, 0, 0, 0, null);
    }

    public SignData(int xOffset, int yOffset, int zOffset, int rotation, @Nullable ReadWriteNBT signCompoundTag) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;

        this.rotation = switch (rotation % 16) {
            case 0 -> BlockFace.NORTH;
            case 1, 2, 3 -> BlockFace.NORTH_EAST;
            case 4 -> BlockFace.EAST;
            case 5, 6, 7 -> BlockFace.SOUTH_EAST;
            case 8 -> BlockFace.SOUTH;
            case 9, 10, 11 -> BlockFace.SOUTH_WEST;
            case 12 -> BlockFace.WEST;
            case 13, 14, 15 -> BlockFace.NORTH_WEST;
            default ->
                    throw new IllegalArgumentException("Unexpected sign rotation: \"" + rotation + "\" (should be within 0-15)");
        };

        if (signCompoundTag != null) {
            for (String key : List.of("Text1", "Text2", "Text3", "Text4")) {
                String rawJson = signCompoundTag.getString(key);
                JsonObject textJson = JsonParser.parseString(rawJson).getAsJsonObject();

                String text = textJson.get("text").getAsString();
                textLines.add(text);
            }
        }
    }

    public boolean isDiagonal() {
        return !rotation.isCartesian();
    }
}
