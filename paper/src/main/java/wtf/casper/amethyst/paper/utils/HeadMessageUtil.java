package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Author: @Demonly, @Casper
 * Head Image util.
 * Gets head image from crafatar, converts to BufferedImage, and then converts pixels into lined TextComponents.
 */

public class HeadMessageUtil {

    private HeadMessageUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the head message of a player.
     * @param uuid The UUID of the player
     * @param size The size of the head
     * @return The head message
     */
    public static CompletableFuture<List<String>> getHeadMessage(UUID uuid, int size) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> lines = new ArrayList<>();

            try {
                Color[][] headColors = getPixelColors(getHeadBufferedImage(uuid, size));

                for (Color[] headColor : headColors) {
                    String line = "";

                    for (Color color : headColor) {
                        line = line + HexUtils.colorify("&#" + Integer.toHexString(color.getRGB()).substring(2) + "█");
                    }

                    lines.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lines;
        });
    }

    /**
     * Send the head message of a player to a player.
     * @param player The player to send the message to
     * @param size The size of the head
     */
    public static void sendHeadMessage(Player player, int size) {
        UUID uuid = player.getUniqueId();

        getHeadMessage(uuid, size).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                for (String line : lines) {
                    player.sendMessage(line);
                }
            }
        });
    }

    /**
     * Send head message to player with custom messages appended.
     * @param player The player to send the message to
     * @param headDelimiter The delimiter to replace with the head message
     * @param centerDelimiter The delimiter to center the message with. If null, the message will not be centered.
     * @param message The message to send
     */
    public static void sendHeadMessage(Player player, String headDelimiter, @Nullable String centerDelimiter, List<String> message) {
        UUID uuid = player.getUniqueId();

        int headCount = 0;
        for (String s : message) {
            if (s.contains(headDelimiter)) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                player.sendMessage(s);
            }
            return;
        }

        getHeadMessage(uuid, headCount).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Placeholders replacer = new Placeholders().centerify("center");
            int headI = 0;
            for (String s : message) {
                if (s.contains(headDelimiter)) {
                    if (centerDelimiter != null && s.contains(centerDelimiter)) {
                        s = s.replace(centerDelimiter, "");
                        s = s.replace(headDelimiter, "");
                        String head = lines.get(headI);
                        String space = StringUtilsPaper.centerMessageSpaces(s, -(head.length() * DefaultFontInfo.DEFAULT.getLength()));
                        s = head + space + s;
                    } else {
                        s = s.replace(headDelimiter, lines.get(headI));
                    }
                    headI++;
                }
                player.sendMessage(HexUtils.colorify(replacer.parse(s)));
            }
        });
    }

    /**
     * Broadcast head message with custom messages appended.
     * @param player The player whose head is being displayed
     * @param headDelimiter The delimiter to replace with the head message
     * @param centerDelimiter The delimiter to center the message with. If null, the message will not be centered.
     * @param message The message to send
     */
    public static void broadcastHeadMessage(UUID player, String headDelimiter, @Nullable String centerDelimiter, List<String> message) {
        int headCount = 0;
        for (String s : message) {
            if (s.contains(headDelimiter)) {
                headCount++;
            }
        }

        if (headCount == 0) {
            for (String s : message) {
                Bukkit.broadcastMessage(s);
            }
            return;
        }

        getHeadMessage(player, headCount).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Placeholders replacer = new Placeholders().centerify("center");
            int headI = 0;
            for (String s : message) {
                if (s.contains(headDelimiter)) {
                    if (centerDelimiter != null && s.contains(centerDelimiter)) {
                        s = s.replace(centerDelimiter, "");
                        s = s.replace(headDelimiter, "");
                        String head = lines.get(headI);
                        String space = StringUtilsPaper.centerMessageSpaces(s, -(head.length() * DefaultFontInfo.DEFAULT.getLength()));
                        s = head + space + s;
                    } else {
                        s = s.replace(headDelimiter, lines.get(headI));
                    }
                    headI++;
                }
                Bukkit.broadcastMessage(HexUtils.colorify(replacer.parse(s)));
            }
        });
    }

    /**
     * Get image message from a file.
     * @param file The file to get the image from
     * @return The image message
     */
    public static CompletableFuture<List<TextComponent>> getFileImageMessage(File file) {
        return CompletableFuture.supplyAsync(() -> {
            List<TextComponent> lines = new ArrayList<>();

            Color[][] headColors;
            try {
                headColors = getPixelColors(getFileBufferedImage(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Color[] headColor : headColors) {
                TextComponent line = Component.empty();

                for (Color color : headColor) {
                    line = line.append(Component.text("█")
                            .color(TextColor.color(color.getRed(), color.getGreen(), color.getBlue())));
                }

                lines.add(line);
            }
            return lines;
        });
    }

    /**
     * Get image message to player from a file.
     * @param file The file to get the image from
     * @return The image message
     */
    public static CompletableFuture<List<String>> getFileImageMessageStr(File file) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> lines = new ArrayList<>();

            Color[][] headColors;
            try {
                headColors = getPixelColors(getFileBufferedImage(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Color[] headColor : headColors) {
                StringBuilder line = new StringBuilder();

                for (Color color : headColor) {
                    line.append(HexUtils.parseHex("&#" + Integer.toHexString(color.getRGB()).substring(2) + "█"));
                }

                lines.add(line.toString());
            }
            return lines;
        });
    }

    /**
     * Send image message to player from a file.
     * @param player The player to send the message to
     * @param file The file to get the image from
     */
    public static void sendFileImageMessage(Player player, File file) {
        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                for (String line : lines) {
                    player.sendMessage(line);
                }
            }
        });
    }

    /**
     * Send image message to player from a file with custom messages appended.
     * @param player The player to send the message to
     * @param file The file to get the image from
     * @param message The message to send
     * @param fileDelimiter The delimiter to replace with the image message
     * @param centerDelimiter The delimiter to center the message with. If null, the message will not be centered.
     * @param replacer The placeholders to replace the message with
     */
    public static void sendFileImageMessage(Player player, File file, List<String> message, String fileDelimiter, String centerDelimiter, Placeholders replacer) {
        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            int headI = 0;
            for (String s : message) {
                if (s.contains(fileDelimiter)) {
                    if (centerDelimiter != null && s.contains(centerDelimiter)) {
                        s = s.replace(centerDelimiter, "");
                        s = s.replace(fileDelimiter, "");
                        String head = lines.get(headI);
                        String space = StringUtilsPaper.centerMessageSpaces(s, -(head.length() * DefaultFontInfo.DEFAULT.getLength()));
                        s = head + space + s;
                    } else {
                        s = s.replace(fileDelimiter, lines.get(headI));
                    }
                    headI++;
                }
                player.sendMessage(HexUtils.colorify(replacer.parse(s)));
            }
        });
    }

    /**
     * Broadcast image message to player from a file.
     * @param file The file to get the image from
     * @param message The message to send
     * @param fileDelimiter The delimiter to replace with the image message
     * @param replacer The placeholders to replace the message with
     */
    public static void broadcastFileImageMessage(File file, List<String> message, String fileDelimiter, String centerDelimiter, Placeholders replacer) {
        getFileImageMessageStr(file).whenComplete((lines, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            int headI = 0;
            for (String s : message) {
                if (s.contains(fileDelimiter)) {
                    if (centerDelimiter != null && s.contains(centerDelimiter)) {
                        s = s.replace(centerDelimiter, "");
                        s = s.replace(fileDelimiter, "");
                        String head = lines.get(headI);
                        String space = StringUtilsPaper.centerMessageSpaces(s, -(head.length() * DefaultFontInfo.DEFAULT.getLength()));
                        s = head + space + s;
                    } else {
                        s = s.replace(fileDelimiter, lines.get(headI));
                    }
                    headI++;
                }
                Bukkit.broadcastMessage(HexUtils.colorify(replacer.parse(s)));
            }
        });
    }

    private static BufferedImage getHeadBufferedImage(UUID uuid) throws IOException {
        return getHeadBufferedImage(uuid, 8);
    }

    private static BufferedImage getHeadBufferedImage(UUID uuid, int size) throws IOException {
        URL url = new URL("https://crafatar.com/avatars/" + uuid + ".png?size=" + size);

        BufferedImage image = ImageIO.read(url);

        // We need to flip and rotate due to ImageIO's shitty nature.
        return flipAndRotate(image);
    }

    private static BufferedImage getFileBufferedImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        // We need to flip and rotate due to ImageIO's shitty nature.
        return flipAndRotate(image);
    }

    private static Color[][] getPixelColors(BufferedImage image) {
        Color[][] colors = new Color[image.getHeight()][image.getWidth()];

        for (int x = 0; x < image.getHeight(); x++) {
            for (int y = 0; y < image.getWidth(); y++) {
                int pixel = image.getRGB(x, y);

                Color color = new Color(pixel, true);

                colors[x][y] = color;
            }
        }

        return colors;
    }

    private static BufferedImage flipAndRotate(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();

        // rotate 90 clockwise
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-Math.PI / 2, w / 2.0, h / 2.0);
        AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage rotated = op.filter(image, null);

        // flip vertically
        affineTransform = AffineTransform.getScaleInstance(1, -1);
        affineTransform.translate(0, -h);
        op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return op.filter(rotated, null);
    }

    private static String centerMessage(String replace, int len) {
        return " ".repeat(len) + replace;
    }

}
