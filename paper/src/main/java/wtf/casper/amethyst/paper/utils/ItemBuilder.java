package wtf.casper.amethyst.paper.utils;

import com.destroystokyo.paper.Namespaced;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder {

    private final static LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().character('&').build();

    private ItemStack stack;
    private final List<Consumer<ItemMeta>> queuedChanges = new ArrayList<>();

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemBuilder(Material material) {
        stack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        stack = new ItemStack(material, amount);
    }

    public ItemBuilder setLegacyName(String name) {
        queuedChanges.add(meta -> meta.displayName(SERIALIZER.deserialize(name)));
        return this;
    }

    public ItemBuilder setName(Component name) {
        queuedChanges.add(meta -> meta.displayName(name));
        return this;
    }

    public ItemBuilder setLegacyLore(List<String> lore) {
        queuedChanges.add(meta -> {
            List<Component> components = new ArrayList<>();
            lore.forEach(line -> components.add(SERIALIZER.deserialize(line)));
            meta.lore(components);
        });
        return this;
    }

    public ItemBuilder setLore(List<Component> lore) {
        queuedChanges.add(meta -> meta.lore(lore));
        return this;
    }

    public ItemBuilder addLegacyLore(String line) {
        queuedChanges.add(meta -> {
            List<Component> components = new ArrayList<>(meta.lore());
            components.add(SERIALIZER.deserialize(line));
            meta.lore(components);
        });
        return this;
    }

    public ItemBuilder addLore(Component line) {
        queuedChanges.add(meta -> {
            List<Component> components = new ArrayList<>(meta.lore());
            components.add(line);
            meta.lore(components);
        });
        return this;
    }

    public ItemBuilder setHeadUrl(String string) {
        queuedChanges.add(meta -> {
            if (meta instanceof SkullMeta) {
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", string));
                try {
                    Field profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(meta, profile);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        queuedChanges.add(meta -> {
            if (meta instanceof SkullMeta) {
                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            }
        });
        return this;
    }

    public ItemBuilder setLeatherColor(int red, int green, int blue) {
        queuedChanges.add(meta -> {
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
            }
        });
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        queuedChanges.add(meta -> {
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(color);
            }
        });
        return this;
    }

    public ItemBuilder setBannerPattern(Pattern pattern) {
        queuedChanges.add(meta -> {
            if (meta instanceof BannerMeta) {
                ((BannerMeta) meta).addPattern(pattern);
            }
        });
        return this;
    }

    public ItemBuilder setBannerPatterns(List<Pattern> patterns) {
        queuedChanges.add(meta -> {
            if (meta instanceof BannerMeta) {
                ((BannerMeta) meta).setPatterns(patterns);
            }
        });
        return this;
    }

    public void setBannerBaseColor(DyeColor color) {
        queuedChanges.add(meta -> {
            if (meta instanceof BannerMeta) {
                ((BannerMeta) meta).setBaseColor(color);
            }
        });
    }

    public ItemBuilder setAmount(int amount) {
        queuedChanges.add(meta -> stack.setAmount(amount));
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        queuedChanges.add(meta -> stack.setDurability(durability));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        queuedChanges.add(meta -> meta.setUnbreakable(unbreakable));
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        queuedChanges.add(meta -> {
            if (glowing) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeEnchant(Enchantment.LUCK);
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        });
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        queuedChanges.add(meta -> meta.addItemFlags(flag));
        return this;
    }

    public ItemBuilder removeItemFlag(ItemFlag flag) {
        queuedChanges.add(meta -> meta.removeItemFlags(flag));
        return this;
    }

    public ItemBuilder addEnchantment(NamespacedKey key, int level) {
        queuedChanges.add(meta -> meta.addEnchant(Enchantment.getByKey(key), level, true));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        queuedChanges.add(meta -> meta.addEnchant(enchantment, level, true));
        return this;
    }

    public ItemBuilder removeEnchantment(NamespacedKey key) {
        queuedChanges.add(meta -> meta.removeEnchant(Enchantment.getByKey(key)));
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        queuedChanges.add(meta -> meta.removeEnchant(enchantment));
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        queuedChanges.add(meta -> meta.setCustomModelData(data));
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        queuedChanges.add(meta -> meta.addAttributeModifier(attribute, modifier));
        return this;
    }

    public ItemBuilder removeAttributeModifier(Attribute attribute) {
        queuedChanges.add(meta -> meta.removeAttributeModifier(attribute));
        return this;
    }

    public ItemBuilder setPlaceOn(List<Namespaced> keys) {
        queuedChanges.add(meta -> meta.setPlaceableKeys(keys));
        return this;
    }

    public ItemBuilder setDestroyableKeys(List<Namespaced> keys) {
        queuedChanges.add(meta -> meta.setDestroyableKeys(keys));
        return this;
    }

    public ItemBuilder clearEnchantments() {
        queuedChanges.add(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
        return this;
    }

    public ItemBuilder clearFlags() {
        queuedChanges.add(meta -> meta.getItemFlags().forEach(meta::removeItemFlags));
        return this;
    }

    public ItemBuilder clearLore() {
        queuedChanges.add(meta -> meta.lore(new ArrayList<>()));
        return this;
    }

    public ItemBuilder clearName() {
        queuedChanges.add(meta -> meta.displayName(null));
        return this;
    }

    public ItemBuilder clearCustomModelData() {
        queuedChanges.add(meta -> meta.setCustomModelData(null));
        return this;
    }

    public ItemBuilder clearAttributes() {
        queuedChanges.add(meta -> meta.getAttributeModifiers().keySet().forEach(meta::removeAttributeModifier));
        return this;
    }

    public ItemBuilder clearPlaceOn() {
        queuedChanges.add(meta -> meta.setPlaceableKeys(new ArrayList<>()));
        return this;
    }

    public ItemBuilder clearDestroyableKeys() {
        queuedChanges.add(meta -> meta.setDestroyableKeys(new ArrayList<>()));
        return this;
    }

    public ItemStack build() {
        ItemStack clone = stack.clone();
        if (queuedChanges.isEmpty()) {
            return clone;
        }

        ItemMeta meta = clone.getItemMeta();
        queuedChanges.forEach(change -> change.accept(meta));
        clone.setItemMeta(meta);
        stack = clone;
        queuedChanges.clear();

        return clone;
    }

    public ItemStack build(Placeholders placeholders) {
        ItemStack clone;
        if (!queuedChanges.isEmpty()) {
            clone = build();
        } else {
            clone = stack.clone();
        }

        ItemMeta meta = clone.getItemMeta();
        meta.displayName(SERIALIZER.deserialize(placeholders.parse(SERIALIZER.serialize(meta.displayName()))));

        List<Component> lore = new ArrayList<>();
        meta.lore().forEach(line -> lore.add(SERIALIZER.deserialize(placeholders.parse(SERIALIZER.serialize(line)))));
        meta.lore(lore);

        clone.setItemMeta(meta);

        return clone;
    }

    public ItemStack build(Placeholders placeholders, OfflinePlayer player) {
        ItemStack clone;
        if (!queuedChanges.isEmpty()) {
            clone = build();
        } else {
            clone = stack.clone();
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return build(placeholders);
        }

        ItemMeta meta = clone.getItemMeta();
        meta.displayName(SERIALIZER.deserialize(PlaceholderAPI.setPlaceholders(player, placeholders.parse(SERIALIZER.serialize(meta.displayName())))));

        List<Component> lore = new ArrayList<>();
        meta.lore().forEach(line -> {
            lore.add(SERIALIZER.deserialize(PlaceholderAPI.setPlaceholders(player, placeholders.parse(SERIALIZER.serialize(line)))));
        });
        meta.lore(lore);

        clone.setItemMeta(meta);
        return clone;
    }

    public static ItemBuilder of(ItemStack stack) {
        return new ItemBuilder(stack);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(ItemBuilder builder) {
        return new ItemBuilder(builder.stack);
    }
}