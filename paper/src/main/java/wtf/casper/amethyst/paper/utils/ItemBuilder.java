package wtf.casper.amethyst.paper.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder extends ItemStack {

    private final static Map<String, NamespacedKey> keysCache = new HashMap<>();

    public ItemBuilder(ItemStack stack) {
        this.setType(stack.getType());
        this.setAmount(stack.getAmount());
        this.setData(stack.getData());
        this.setItemMeta(stack.getItemMeta());
    }

    public ItemBuilder(Material mat) {
        super(mat);
    }

    public ItemBuilder itemMeta(ItemMeta meta) {
        this.setItemMeta(meta);
        return this;
    }

    public ItemBuilder getStack(PlaceholderReplacer replacer) {
        ItemMeta meta = this.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(replacer.parse(meta.getDisplayName()));
        }
        if (meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            for (String s : meta.getLore()) {
                lore.add(replacer.parse(s));
            }
            meta.setLore(lore);
        }
        this.setItemMeta(meta);
        return this;
    }

    public ItemBuilder getStack(Player player, PlaceholderReplacer replacer) {
        ItemMeta meta = this.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(StringUtilsPaper.parsePlaceholders(meta.getDisplayName(), replacer, player));
        }
        if (meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            for (String s : meta.getLore()) {
                lore.add(StringUtilsPaper.parsePlaceholders(s, replacer, player));
            }
            meta.setLore(lore);
        }
        this.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) this.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        BannerMeta meta = (BannerMeta) this.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHeadUrl(String url) {
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        this.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) this.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder builderLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.setType(material);
        return this;
    }

    public Material getMaterial() {
        return getType();
    }

    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = getItemMeta();
        meta.setCustomModelData(data);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.removeItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public NamespacedKey keyFromCache(String key) {
        if (keysCache.containsKey(key)) {
            return keysCache.get(key);
        }
        NamespacedKey namespacedKey = new NamespacedKey("amethyst", key);
        keysCache.put(key, namespacedKey);
        return namespacedKey;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this);
    }
}