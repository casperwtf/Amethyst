package wtf.casper.amethyst.paper.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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

public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
    }

    @Deprecated
    public ItemBuilder(Material mat, short sh) {
        stack = new ItemStack(mat, 1, sh);
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ItemStack getStack(PlaceholderReplacer replacer) {
        ItemStack stack = this.stack.clone();
        ItemMeta meta = stack.getItemMeta();
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
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getStack(Player player, PlaceholderReplacer replacer) {
        ItemStack stack = this.stack.clone();
        ItemMeta meta = stack.getItemMeta();
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
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
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
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        BannerMeta meta = (BannerMeta) stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setHeadUrl(String url) {
        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
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
        stack.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
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

    public ItemBuilder setItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        stack.setType(material);
        return this;
    }

    public Material getMaterial() {
        return stack.getType();
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

    public ItemBuilder addNBT(String key, Object value) {
        NBT.modify(stack, readWriteItemNBT -> {
            switch (value.getClass().getSimpleName()) {
                case "String" -> readWriteItemNBT.setString(key, (String) value);
                case "Integer" -> readWriteItemNBT.setInteger(key, (Integer) value);
                case "Double" -> readWriteItemNBT.setDouble(key, (Double) value);
                case "Float" -> readWriteItemNBT.setFloat(key, (Float) value);
                case "Long" -> readWriteItemNBT.setLong(key, (Long) value);
                case "Short" -> readWriteItemNBT.setShort(key, (Short) value);
                case "Byte" -> readWriteItemNBT.setByte(key, (Byte) value);
                case "Boolean" -> readWriteItemNBT.setBoolean(key, (Boolean) value);
                case "int[]" -> readWriteItemNBT.setIntArray(key, (int[]) value);
                case "byte[]" -> readWriteItemNBT.setByteArray(key, (byte[]) value);
                case "UUID" -> readWriteItemNBT.setUUID(key, (UUID) value);
                case "ItemStack" -> readWriteItemNBT.setItemStack(key, (ItemStack) value);
                case "ItemStack[]" -> readWriteItemNBT.setItemStackArray(key, (ItemStack[]) value);
            }
        });
        return this;
    }

    public ItemBuilder removeNBT(String key) {
        NBT.modify(stack, readWriteItemNBT -> {
            readWriteItemNBT.removeKey(key);
        });
        return this;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.stack);
    }
}