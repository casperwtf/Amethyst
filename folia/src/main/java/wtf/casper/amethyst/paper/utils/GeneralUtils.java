package wtf.casper.amethyst.paper.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.AmethystFolia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class GeneralUtils {

    private static String serverJar = null;

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

    @SneakyThrows
    public static String getServerJar(final File log) {
        if (serverJar != null) {
            return serverJar;
        }

        final BufferedReader reader = new BufferedReader(new FileReader(log));

        final String[] jar = new String[1];
        jar[0] = "Unknown";
        reader.lines().parallel().forEach(string -> {

            if (!string.contains("This server is running")) {
                return;
            }

            jar[0] = string;
        });

        serverJar = jar[0];
        return serverJar;
    }

    @Nullable
    public static String readLog(File log) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(log.toPath());
        } catch (IOException e) {
            return null;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static boolean isBlockPlacedByPlayer(Block block) {
        PersistentDataContainer customBlockData = new CustomBlockData(block, AmethystFolia.getInstance());
        return customBlockData.has(AmethystFolia.getInstance().getPlayerPlacedBlockKey(), PersistentDataType.BYTE);
    }
}
