package wtf.casper.amethyst.paper.utils;

import com.mojang.authlib.GameProfile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder extends ItemStack {

    public ItemBuilder(Material material) {
        super(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    public ItemBuilder(Material material, int amount) {
        super(material, amount);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        super(material, amount, damage);
    }


    public ItemBuilder(Material material, int amount, short damage, String displayName) {
        super(material, amount, damage);
        modifyMeta(meta -> meta.setDisplayName(displayName));
    }

    public ItemBuilder(Material material, int amount, short damage, String displayName, String... lore) {
        super(material, amount, damage);
        modifyMeta(meta -> {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(lore));
        });
    }

    public ItemBuilder(Material material, int amount, short damage, Consumer<ItemMeta> metaConsumer) {
        super(material, amount, damage);
        modifyMeta(metaConsumer);
    }

    public ItemBuilder setSkullOwner(String owner) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.SkullMeta) meta).setOwner(owner));
        return this;
    }

    public ItemBuilder addLore(String lore) {
        modifyMeta(meta -> {
            if (meta.getLore() == null) {
                meta.setLore(new java.util.ArrayList<>());
            }
            meta.getLore().add(lore);
        });
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        modifyMeta(meta -> {
            if (meta.getLore() == null) {
                meta.setLore(new java.util.ArrayList<>());
            }
            meta.getLore().addAll(java.util.Arrays.asList(lore));
        });

        return this;
    }

    public ItemBuilder setLeatherArmorColor(int red, int green, int blue) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.LeatherArmorMeta) meta).setColor(org.bukkit.Color.fromRGB(red, green, blue)));
        return this;
    }

    public ItemBuilder setLeatherArmorColor(org.bukkit.Color color) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.LeatherArmorMeta) meta).setColor(color));
        return this;
    }

    public ItemBuilder setBannerColor(org.bukkit.DyeColor color) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.BannerMeta) meta).setBaseColor(color));
        return this;
    }

    public ItemBuilder setBannerPattern(org.bukkit.block.banner.Pattern pattern) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.BannerMeta) meta).addPattern(pattern));
        return this;
    }

    public ItemBuilder setBannerPatterns(org.bukkit.block.banner.Pattern... patterns) {
        modifyMeta(meta -> {
            for (org.bukkit.block.banner.Pattern pattern : patterns) {
                ((org.bukkit.inventory.meta.BannerMeta) meta).addPattern(pattern);
            }
        });
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        modifyMeta(meta -> meta.setUnbreakable(unbreakable));
        return this;
    }

    public ItemBuilder modifyMeta(Consumer<ItemMeta> metaConsumer) {
        ItemMeta itemMeta = getItemMeta();
        metaConsumer.accept(itemMeta);
        setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder replace(Placeholders placeholders) {
        modifyMeta(meta -> {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(placeholders.parse(meta.getDisplayName()));
            }

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String s : meta.getLore()) {
                    lore.set(lore.indexOf(s), placeholders.parse(s));
                }
                meta.setLore(lore);
            }
        });

        return this;
    }

    public ItemBuilder replace(Player player, Placeholders placeholders) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            replace(placeholders);
            return this;
        }

        modifyMeta(meta -> {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(placeholders.parse(PlaceholderAPI.setPlaceholders(player, meta.getDisplayName())));
            }

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String s : meta.getLore()) {
                    lore.set(lore.indexOf(s), placeholders.parse(PlaceholderAPI.setPlaceholders(player, s)));
                }
                meta.setLore(lore);
            }
        });

        return this;
    }

    public void setHeadUrl(String string) {
        modifyMeta(meta -> {
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", string));
                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addItemFlag(ItemFlag itemFlag) {
        modifyMeta(meta -> meta.addItemFlags(itemFlag));
    }

    public void addEnchant(Enchantment byKey, int lvl) {
        addUnsafeEnchantment(byKey, lvl);
    }

    public void addAttribute(Attribute attribute, AttributeModifier modifier) {
        modifyMeta(meta -> meta.addAttributeModifier(attribute, modifier));
    }

    public void setColor(Color color) {
        modifyMeta(meta -> ((org.bukkit.inventory.meta.LeatherArmorMeta) meta).setColor(color));
    }

    public void setCustomModelData(Integer integer) {
        modifyMeta(meta -> meta.setCustomModelData(integer));
    }

    @Override
    public @NotNull ItemBuilder clone() {
        return new ItemBuilder(super.clone());
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }
}