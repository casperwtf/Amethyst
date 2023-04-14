package wtf.casper.amethyst.paper.serialized;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

@Data
@RequiredArgsConstructor
public class SerializablePotionMeta {

    private final PotionType basePotionType;
    private final boolean upgraded;
    private final boolean extended;
    private final int rgbColor;

    public SerializablePotionMeta(PotionMeta potionMeta) {
        PotionData potionData = potionMeta.getBasePotionData();
        basePotionType = potionData.getType();
        upgraded = basePotionType.isUpgradeable() && potionData.isUpgraded();
        extended = basePotionType.isExtendable() && potionData.isExtended();

        Color color = potionMeta.getColor();
        rgbColor = color != null ? color.asRGB() : -1;
    }

    public static SerializablePotionMeta deserialize(JsonObject jsonObject) {
        PotionType basePotionType = PotionType.valueOf(jsonObject.get("base-type").getAsString());
        boolean upgraded = jsonObject.get("upgraded").getAsBoolean();
        boolean extended = jsonObject.get("extended").getAsBoolean();
        int rgbColor = jsonObject.has("rgb-color") ? jsonObject.get("rgb-color").getAsInt() : -1;
        return new SerializablePotionMeta(basePotionType, upgraded, extended, rgbColor);
    }

    public void apply(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(new PotionData(basePotionType,
                    basePotionType.isExtendable() && extended,
                    basePotionType.isUpgradeable() && upgraded));
            if (rgbColor > -1) {
                potionMeta.setColor(Color.fromRGB(rgbColor));
            }
            itemStack.setItemMeta(potionMeta);
        }
    }

    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("base-type", basePotionType.name());
        jsonObject.addProperty("upgraded", upgraded);
        jsonObject.addProperty("extended", extended);
        if (rgbColor > -1) {
            jsonObject.addProperty("rgb-color", rgbColor);
        }
        return jsonObject;
    }

}
