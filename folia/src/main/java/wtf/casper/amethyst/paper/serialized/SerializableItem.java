package wtf.casper.amethyst.paper.serialized;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import wtf.casper.amethyst.paper.utils.StringUtilsPaper;

import java.util.*;

@Getter
@Setter
@ToString
public class SerializableItem {

    private final transient ItemStack itemStack;
    private Material type;
    private int amount;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private String name;
    private List<String> lore;
    private int customModelData;
    private SerializablePlayerProfile skullPlayerProfile;
    private OfflinePlayer skullUser;
    private SerializablePotionMeta potionMeta;
    private String nbtDataString;
    private int damage = -1;

    public SerializableItem(Material type, int amount, String name, List<String> lore, int customModelData, SerializablePlayerProfile skullPlayerProfile, OfflinePlayer skullUser) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.customModelData = customModelData;
        this.skullPlayerProfile = skullPlayerProfile;
        this.skullUser = skullUser;
        this.nbtDataString = null;

        this.itemStack = new ItemStack(type);
        itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            this.name = meta.getDisplayName();
            this.lore = meta.getLore();
            if (meta.hasCustomModelData()) {
                this.customModelData = meta.getCustomModelData();
            } else {
                this.customModelData = -1;
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                PlayerProfile profile = skullMeta.getPlayerProfile();
                if (profile != null) { // Can't put null profile in the serialization wrapper
                    this.skullPlayerProfile = new SerializablePlayerProfile(profile);
                }
                this.skullUser = skullMeta.getOwningPlayer();

            }

            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                enchantments.putAll(enchantmentStorageMeta.getStoredEnchants());
            }

            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                damage = damageable.getDamage();
            }

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                this.potionMeta = new SerializablePotionMeta(potionMeta);
            }
        }

        itemStack.setItemMeta(meta);

    }

    /**
     * A wrapper for {@link ItemStack} which permits serialization into Json (and Gson using {@link SerializableItemTypeAdapter})
     *
     * @param itemStack to be wrapped
     */
    public SerializableItem(ItemStack itemStack) {
        this.type = itemStack.getType();
        this.amount = itemStack.getAmount();

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            this.name = meta.getDisplayName();
            this.lore = meta.getLore();
            if (meta.hasCustomModelData()) {
                this.customModelData = meta.getCustomModelData();
            } else {
                this.customModelData = -1;
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                PlayerProfile profile = skullMeta.getPlayerProfile();
                if (profile != null) { // Can't put null profile in the serialization wrapper
                    skullPlayerProfile = new SerializablePlayerProfile(profile);
                }
                skullUser = skullMeta.getOwningPlayer();
            }

            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                enchantments.putAll(enchantmentStorageMeta.getStoredEnchants());
            }

            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                damage = damageable.getDamage();
            }

            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                this.potionMeta = new SerializablePotionMeta(potionMeta);
            }
        }

        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasNBTData()) {
            this.nbtDataString = nbtItem.toString();
        }

        enchantments.putAll(itemStack.getEnchantments());
        this.itemStack = itemStack;
    }

    /**
     * Takes a Json and deserializes it into this wrapper
     *
     * @param document the Json to deserialize
     * @return the deserialized object
     */
    public static SerializableItem deserialize(JsonObject document) {
        Material material = Material.valueOf(document.get("type").getAsString());
        int amount = document.get("amount").getAsInt();

        String name = null;
        if (document.has("name")) { // Could potentially not exist if null in wrapper
            name = StringUtilsPaper.colorify(document.get("name").getAsString());
        }

        List<String> lore = new ArrayList<>();
        for (JsonElement loreElement : document.getAsJsonArray("lore")) {
            String loreLine = loreElement.getAsString();
            lore.add(StringUtilsPaper.colorify(loreLine));
        }

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }

            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }

            if (document.has("custom-model-data")) {
                meta.setCustomModelData(document.get("custom-model-data").getAsInt());
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (document.has("skull-profile")) {
                    SerializablePlayerProfile serializedProfile = SerializablePlayerProfile.deserialize(document.getAsJsonObject("skull-profile"));
                    skullMeta.setPlayerProfile(serializedProfile.getPlayerProfile());
                }
                if (document.has("skull-user")) {
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(document.get("skull-user").getAsString())));
                }
            }

            if (document.has("damage") && meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                damageable.setDamage(document.get("damage").getAsInt());
            }

            if (document.has("potion-meta") && meta instanceof PotionMeta) {
                SerializablePotionMeta serializablePotionMeta = SerializablePotionMeta.deserialize(document.getAsJsonObject("potion-meta"));
                serializablePotionMeta.apply(item);
            }

            item.setItemMeta(meta);
        }

        JsonArray enchantmentsDocument = document.getAsJsonArray("enchantments");
        Map<Enchantment, Integer> enchantmentMap = new HashMap<>();
        for (JsonElement rawEnchantmentProperties : enchantmentsDocument) {
            JsonObject enchantmentProperties = rawEnchantmentProperties.getAsJsonObject();
            String key = enchantmentProperties.get("enchantment-name").getAsString();
            int level = enchantmentProperties.get("enchantment-level").getAsInt();
            if (level < 1) {
                level = 1;
            }
            enchantmentMap.put(Enchantment.getByKey(NamespacedKey.minecraft(key)), level);
        }

        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> enchantmentEntry : enchantmentMap.entrySet()) {
                Enchantment enchantment = enchantmentEntry.getKey();
                if (!enchantmentStorageMeta.hasConflictingStoredEnchant(enchantment)) { // Prevent conflicts (shouldn't be any but never know)
                    enchantmentStorageMeta.addStoredEnchant(enchantmentEntry.getKey(), enchantmentEntry.getValue(), false);
                }
            }
            item.setItemMeta(meta);
        } else {
            item.addUnsafeEnchantments(enchantmentMap);
        }

        if (document.has("nbt-data") && item.getType() != Material.AIR) {
            NBTContainer nbtContainer = new NBTContainer(document.get("nbt-data").getAsString());
            ItemStack nbtConverted = NBTItem.convertNBTtoItem(nbtContainer);
            if (nbtConverted.getType() != Material.AIR) {
                NBTItem nbtItem = new NBTItem(nbtConverted);
                nbtItem.applyNBT(item);
            }
        }

        return new SerializableItem(item);
    }

    public void applySkullData() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullPlayerProfile != null) {
                skullMeta.setPlayerProfile(skullPlayerProfile.getPlayerProfile());
            }
            if (skullUser != null) {
                skullMeta.setOwningPlayer(skullUser);
            }
            itemStack.setItemMeta(skullMeta);
        }
    }

    /**
     * Serializes the item into Json format
     *
     * @return the Json serialized item
     */
    public JsonObject serialize() {
        JsonObject document = new JsonObject();
        document.addProperty("type", type.name());
        document.addProperty("amount", amount);
        if (this.name != null) { // Name is in ItemMeta so could be null
            document.addProperty("name", name.replace(ChatColor.COLOR_CHAR, '&'));
        }

        if (customModelData > -1) {
            document.addProperty("custom-model-data", customModelData);
        }

        JsonArray loreArray = new JsonArray();
        if (this.lore != null) {
            for (String loreString : this.lore) {
                loreArray.add(loreString.replace(ChatColor.COLOR_CHAR, '&'));
            }
        }
        document.add("lore", loreArray);

        if (potionMeta != null) {
            document.add("potion-meta", potionMeta.serialize());
        }

        JsonArray enchantmentJson = new JsonArray();
        for (Map.Entry<Enchantment, Integer> rawEnchantment : enchantments.entrySet()) {
            JsonObject enchantmentProperties = new JsonObject();
            String enchantmentKey = rawEnchantment.getKey().getKey().getKey();
            int level = rawEnchantment.getValue();
            if (level < 1) {
                level = 1;
            }
            enchantmentProperties.addProperty("enchantment-name", enchantmentKey);
            enchantmentProperties.addProperty("enchantment-level", level);
            enchantmentJson.add(enchantmentProperties);
        }
        document.add("enchantments", enchantmentJson);

        if (skullPlayerProfile != null) {
            document.add("skull-profile", skullPlayerProfile.serialize());
        }
        if (skullUser != null) {
            document.addProperty("skull-user", skullUser.getUniqueId().toString());
        }

        if (damage > -1) {
            document.addProperty("damage", damage);
        }

        if (nbtDataString != null) {
            document.addProperty("nbt-data", nbtDataString);
        }

        return document;
    }

    public ItemStack build() {
        ItemStack itemStack1 = new ItemStack(type, amount);
        ItemMeta meta = itemStack1.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        if (customModelData > -1) {
            meta.setCustomModelData(customModelData);
        }
        if (damage > -1) {
            ((Damageable) meta).setDamage(damage);
        }

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullPlayerProfile != null) {
                skullMeta.setPlayerProfile(skullPlayerProfile.getPlayerProfile());
            }
            if (skullUser != null) {
                skullMeta.setOwningPlayer(skullUser);
            }
        }

        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            if (this.potionMeta != null) {
                this.potionMeta.apply(itemStack1);
            }
        }

        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> enchantmentEntry : enchantments.entrySet()) {
                Enchantment enchantment = enchantmentEntry.getKey();
                if (!enchantmentStorageMeta.hasConflictingStoredEnchant(enchantment)) { // Prevent conflicts (shouldn't be any but never know)
                    enchantmentStorageMeta.addStoredEnchant(enchantmentEntry.getKey(), enchantmentEntry.getValue(), false);
                }
            }
        } else {
            itemStack1.addUnsafeEnchantments(enchantments);
        }

        itemStack1.setItemMeta(meta);

        if (nbtDataString != null) {
            NBTContainer nbtContainer = new NBTContainer(nbtDataString);
            ItemStack nbtConverted = NBTItem.convertNBTtoItem(nbtContainer);
            if (nbtConverted.getType() != Material.AIR) {
                NBTItem nbtItem = new NBTItem(nbtConverted);
                nbtItem.applyNBT(itemStack1);
            }
        }

        return itemStack1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SerializableItem) {
            SerializableItem serializableItem = (SerializableItem) obj;
            return serializableItem.serialize().equals(this.serialize());
        }
        if (obj instanceof ItemStack) {
            return this.build().equals(obj);
        }
        return false;
    }

    public boolean softEquals(Object obj) {
        if (obj instanceof SerializableItem serObj) {
            return isSimilar(serObj);
        }
        if (obj instanceof ItemStack) {
            return isSimilar(new SerializableItem((ItemStack) obj));
        }
        return false;
    }

    public boolean isSimilar(SerializableItem item) {

        return item.getType() == this.getType()
                && Objects.equals(item.getName(), this.getName())
                && Objects.equals(item.getLore(), this.getLore())
                && item.getDamage() == this.getDamage()
                && item.getCustomModelData() == this.getCustomModelData()
                && Objects.equals(item.getEnchantments(), this.getEnchantments())
                && Objects.equals(item.getSkullPlayerProfile(), this.getSkullPlayerProfile())
                && Objects.equals(item.getSkullUser(), this.getSkullUser())
                && Objects.equals(item.getPotionMeta(), this.getPotionMeta())
                && Objects.equals(item.getNbtDataString(), this.getNbtDataString());

    }
}
