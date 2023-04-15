package wtf.casper.amethyst.paper.utils;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.core.exceptions.AmethystException;
import wtf.casper.amethyst.core.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.Locale;

public class ItemConfigUtils {

    public static ItemBuilder getItemBuilder(Section section, @Nullable OfflinePlayer player, @Nullable PlaceholderReplacer replacer) {

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
            if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            }
            if (replacer != null) {
                s = replacer.parse(s);
            }

            builder.setDisplayName(StringUtilsPaper.colorify(s));
        });
        section.getOptionalStringList("lore").ifPresent(lore -> {
            if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                lore = PlaceholderAPI.setPlaceholders(player, lore);
            }
            if (replacer != null) {
                lore.replaceAll(replacer::parse);
            }

            builder.setLore(StringUtilsPaper.colorify(lore));
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
                builder.addNBT(section1.getString(key + ".key"), section1.get(key + ".value"));
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

    public static ItemBuilder getItemBuilder(Section section, PlaceholderReplacer replacer) {
        return getItemBuilder(section, null, replacer);
    }

    public static ItemBuilder getItemBuilder(Section section) {
        return getItemBuilder(section, null, null);
    }

    public static ItemStack getItem(Section section, OfflinePlayer player, PlaceholderReplacer replacer) {
        return getItemBuilder(section, player, replacer).getStack();
    }

    public static ItemStack getItem(Section section, OfflinePlayer player) {
        return getItem(section, player, null);
    }

    public static ItemStack getItem(Section section, PlaceholderReplacer replacer) {
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

}
