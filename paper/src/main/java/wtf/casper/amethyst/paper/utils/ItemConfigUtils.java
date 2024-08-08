package wtf.casper.amethyst.paper.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import wtf.casper.amethyst.core.utils.MathUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ItemConfigUtils {

    private ItemConfigUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Expected Section Format: <br>
     * <pre>
     * {@code
     *<item-section>:
     *  material: ENUM (MATERIAL)
     *  head-url: STRING (Mojang Profile Id)
     *  name: STRING
     *  lore:
     *  - STRING
     *  item-flags:
     *  - ENUM (ItemFlag)
     *  enchantments:
     *  - STRING (namespace:enchant-name:level)
     *  attributes:
     *      <attribute>:
     *          uuid: STRING (UUID)
     *          name: STRING
     *          amount: DOUBLE
     *          operation: ENUM (AttributeModifier.Operation)
     *          slot: ENUM (EquipmentSlot)
     *          * (repeat for each attribute)
     *  unbreakable: BOOLEAN
     *  amount: INT
     *  color: INT
     *  custom-model-data: INT
     * }
     * </pre>
     * @param section The section to get the item from
     * @param player The player to replace placeholders for
     * @param replacer The replacer to replace placeholders with
     * @return The itemstack from the section
     */
    public static ItemBuilder getItemBuilder(Section section, @Nullable OfflinePlayer player, @Nullable Placeholders replacer) {

        if (!section.contains("material")) {
            throw new RuntimeException("Material is not defined in the config for " + section.getRouteAsString());
        }

        if (!StringUtilsPaper.validateEnum(section.getString("material").toUpperCase(Locale.ROOT), Material.class)) {
            throw new RuntimeException("Material is not valid in the config for " + section.getRouteAsString());
        }

        Material material = Material.valueOf(section.getString("material").toUpperCase(Locale.ROOT));
        ItemBuilder builder = new ItemBuilder(material);

        if (material == Material.PLAYER_HEAD) {
            section.getOptionalString("head-url").ifPresent(builder::setHeadUrl);
        }

        section.getOptionalString("name").ifPresent(s -> {

            if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            }

            if (replacer != null) {
                s = replacer.parse(s);
            }

            ItemMeta meta = builder.getItemMeta();
            meta.setDisplayName(s);
            builder.setItemMeta(meta);
        });

        section.getOptionalStringList("lore").ifPresent(lore -> {
            ItemMeta meta = builder.getItemMeta();
            List<String> components = new ArrayList<>();
            for (String s : lore) {
                if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    s = PlaceholderAPI.setPlaceholders(player, s);
                }

                if (replacer != null) {
                    s = replacer.parse(s);
                }

                components.add(s);
            }

            meta.setLore(components);
            builder.setItemMeta(meta);
        });

        section.getOptionalStringList("item-flags").ifPresent(itemFlags -> {
            for (String itemFlag : itemFlags) {
                if (StringUtilsPaper.validateEnum(itemFlag.toUpperCase(Locale.ROOT), ItemFlag.class)) {
                    builder.addItemFlag(ItemFlag.valueOf(itemFlag.toUpperCase(Locale.ROOT)));
                }
            }
        });

        section.getOptionalStringList("enchantments").ifPresent(enchantments -> {
            for (String enchants : enchantments) {
                String[] enchantName = enchants.split(":");
                if (enchantName.length != 3) continue;
                if (MathUtils.validateInt(enchantName[2])) {
                    builder.addEnchant(Enchantment.getByKey(NamespacedKey.fromString(enchantName[0] + ":" + enchantName[1])), Integer.parseInt(enchantName[2]));
                }
            }
        });

        section.getOptionalSection("attributes").ifPresent(section1 -> {
            for (String key : section1.getRoutesAsStrings(false)) {
                Section attributeModifier = section1.getSection(key);
                AttributeModifier modifier = new AttributeModifier(
                        attributeModifier.getOptionalString("uuid").map(UUID::fromString).orElse(UUID.randomUUID()),
                        attributeModifier.getString("name"),
                        attributeModifier.getDouble("amount"),
                        AttributeModifier.Operation.valueOf(attributeModifier.getString("operation")),
                        attributeModifier.getOptionalString("slot").map(string -> {
                            if (StringUtilsPaper.validateEnum(string.toUpperCase(Locale.ROOT), EquipmentSlot.class)) {
                                return EquipmentSlot.valueOf(string.toUpperCase(Locale.ROOT));
                            }
                            return null;
                        }).orElse(null)
                );
                builder.addAttribute(Attribute.valueOf(key.toUpperCase(Locale.ROOT)), modifier);
            }
        });

        section.getOptionalBoolean("unbreakable").ifPresent(builder::setUnbreakable);
        section.getOptionalInt("amount").ifPresent(builder::setAmount);
        section.getOptionalInt("color").ifPresent(color -> builder.setColor(Color.fromRGB(color)));
        section.getOptionalInt("custom-model-data").ifPresent(builder::setCustomModelData);

        return builder;
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @param player The player to replace placeholders for
     * @return The itemstack from the section
     */
    public static ItemBuilder getItemBuilder(Section section, OfflinePlayer player) {
        return getItemBuilder(section, player, null);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @param replacer The replacer to replace placeholders with
     * @return The itemstack from the section
     */
    public static ItemBuilder getItemBuilder(Section section, Placeholders replacer) {
        return getItemBuilder(section, null, replacer);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @return The itemstack from the section
     */
    public static ItemBuilder getItemBuilder(Section section) {
        return getItemBuilder(section, null, null);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @param player The player to replace placeholders for
     * @param replacer The replacer to replace placeholders with
     * @return The itemstack from the section
     */
    public static ItemStack getItem(Section section, OfflinePlayer player, Placeholders replacer) {
        return getItemBuilder(section, player, replacer);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @param player The player to replace placeholders for
     * @return The itemstack from the section
     */
    public static ItemStack getItem(Section section, OfflinePlayer player) {
        return getItem(section, player, null);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @param replacer The replacer to replace placeholders with
     * @return The itemstack from the section
     */
    public static ItemStack getItem(Section section, Placeholders replacer) {
        return getItem(section, null, replacer);
    }

    /**
     * See {@link #getItemBuilder(Section, OfflinePlayer, Placeholders)}
     * @param section The section to get the item from
     * @return The itemstack from the section
     */
    public static ItemStack getItem(Section section) {
        return getItem(section, null, null);
    }

    /**
     * Checks if the inventory is full
     * @param inventory The inventory to check
     * @return If the inventory is full
     */
    public static boolean isFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    /**
     * Checks if the inventory is full after adding the item
     * @param inventory The inventory to check
     * @param toAdd The item to add
     * @return If the inventory is full after adding the item
     */
    public static boolean isFull(Inventory inventory, ItemStack toAdd) {
        if (!isFull(inventory)) {
            return false;
        }

        int leftToAdd = toAdd.getAmount();
        for (ItemStack itemStack : inventory) {
            if (!itemStack.equals(toAdd)) {
                continue;
            }
            if (leftToAdd - (itemStack.getMaxStackSize() - itemStack.getAmount()) <= 0) {
                return false;
            }
            leftToAdd -= itemStack.getMaxStackSize() - itemStack.getAmount();
        }
        return true;
    }

    /**
     * Returns the amount of free slots in the inventory
     * @param inventory
     * @return the amount of free slots in the inventory
     */
    public static int freeSlots(Inventory inventory) {
        int freeSlots = 0;
        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                freeSlots++;
            }
        }
        return freeSlots;
    }

    /**
     * Returns the amount of free slots in the inventory after adding the item
     * @param inventory The inventory to check
     * @param toAdd The item to add
     * @return the amount of free slots in the inventory after adding the item
     */
    public static int freeSlots(Inventory inventory, ItemStack toAdd) {
        int freeSlots = 0;
        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                freeSlots += toAdd.getMaxStackSize();
            } else if (itemStack.equals(toAdd)) {
                freeSlots += itemStack.getMaxStackSize() - itemStack.getAmount();
            }
        }
        return freeSlots;
    }

    /**
     * @param headURL The head URL to get the custom skull from
     * @return The custom skull itemstack
     */
    public static ItemStack getCustomSkull(String headURL) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (headURL.isEmpty())
            return head;
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", headURL));
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        head.setItemMeta(skullMeta);
        return head;
    }

}
