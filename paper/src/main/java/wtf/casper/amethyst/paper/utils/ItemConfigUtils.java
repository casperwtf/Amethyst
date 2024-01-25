package wtf.casper.amethyst.paper.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.text.Component;
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
import org.bukkit.persistence.PersistentDataType;
import wtf.casper.amethyst.core.exceptions.AmethystException;
import wtf.casper.amethyst.core.utils.MathUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ItemConfigUtils {

    public static ItemBuilder getItemBuilder(Section section, @Nullable OfflinePlayer player, @Nullable Placeholders replacer) {

        if (!section.contains("material")) {
            try {
                throw new AmethystException("Material is not defined in the config for " + section.getRouteAsString());
            } catch (AmethystException e) {
                throw new RuntimeException(e);
            }
        }

        if (!StringUtilsPaper.validateEnum(section.getString("material").toUpperCase(Locale.ROOT), Material.class)) {
            try {
                throw new AmethystException("Material is not valid in the config for " + section.getRouteAsString());
            } catch (AmethystException e) {
                throw new RuntimeException(e);
            }
        }

        Material material = Material.valueOf(section.getString("material").toUpperCase(Locale.ROOT));
        ItemBuilder builder = new ItemBuilder(material);

        if (material == Material.PLAYER_HEAD) {
            section.getOptionalString("headURL").ifPresent(builder::setHeadUrl);
        }

        section.getOptionalString("name").ifPresent(s -> {

            Component component = StringUtilsPaper.parseMini(s, player, replacer);
            if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                component = StringUtilsPaper.parsePAPI(component, player);
            }

            ItemMeta meta = builder.getItemMeta();
            meta.displayName(component);
            builder.setItemMeta(meta);
        });

        section.getOptionalStringList("lore").ifPresent(lore -> {
            ItemMeta meta = builder.getItemMeta();
            List<Component> components = new ArrayList<>();
            for (String s : lore) {
                Component component = StringUtilsPaper.parseMini(s, player, replacer);
                if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    component = StringUtilsPaper.parsePAPI(component, player);
                }
                components.add(component);
            }

            builder.setItemMeta(meta);
        });

        section.getOptionalStringList("itemFlags").ifPresent(itemFlags -> {
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

        section.getOptionalSection("nbt").ifPresent(section1 -> {
            for (String key : section1.getRoutesAsStrings(false)) {
                Object oTemp = section1.get(key + ".value");
                if (oTemp instanceof String o) {
                    builder.getItemMeta().getPersistentDataContainer().set(builder.keyFromCache(section1.getString(key + ".key")), PersistentDataType.STRING, o);
                } else if (oTemp instanceof Boolean o) {
                    builder.getItemMeta().getPersistentDataContainer().set(builder.keyFromCache(section1.getString(key + ".key")), PersistentDataType.BOOLEAN, o);
                } else if (oTemp instanceof Number o) {
                    builder.getItemMeta().getPersistentDataContainer().set(builder.keyFromCache(section1.getString(key + ".key")), PersistentDataType.DOUBLE, o.doubleValue());
                } else {
                    throw new IllegalStateException("Unknown type for NBT value. " + oTemp.getClass().getName() + " for " + section1.getRouteAsString());
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
        section.getOptionalInt("customModelData").ifPresent(builder::setCustomModelData);

        return builder;
    }

    public static ItemBuilder getItemBuilder(Section section, OfflinePlayer player) {
        return getItemBuilder(section, player, null);
    }

    public static ItemBuilder getItemBuilder(Section section, Placeholders replacer) {
        return getItemBuilder(section, null, replacer);
    }

    public static ItemBuilder getItemBuilder(Section section) {
        return getItemBuilder(section, null, null);
    }

    public static ItemStack getItem(Section section, OfflinePlayer player, Placeholders replacer) {
        return getItemBuilder(section, player, replacer);
    }

    public static ItemStack getItem(Section section, OfflinePlayer player) {
        return getItem(section, player, null);
    }

    public static ItemStack getItem(Section section, Placeholders replacer) {
        return getItem(section, null, replacer);
    }

    public static ItemStack getItem(Section section) {
        return getItem(section, null, null);
    }

    public static boolean isFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    public static boolean isFull(Inventory inventory, ItemStack toAdd) {
        if (isFull(inventory)) {
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
        return false;
    }

    public static int freeSlots(Inventory inventory) {
        int freeSlots = 0;
        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                freeSlots += 64;
            }
        }
        return freeSlots;
    }

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
