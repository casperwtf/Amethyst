package wtf.casper.amethyst.paper.utils;

import de.themoep.minedown.adventure.MineDown;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import wtf.casper.amethyst.core.utils.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class StringUtilsPaper extends StringUtils {

    public static void broadcast(YamlDocument configuration, String path, PlaceholderReplacer placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> broadcast(strings, null, placeholderReplacer));
            return;
        }

        configuration.getOptionalString(path + ".message").ifPresent(s -> broadcast(s, null, placeholderReplacer));

    }

    public static void broadcast(@Nullable List<String> message, @Nullable List<String> geyser, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(message, geyser, player, placeholderReplacer);
        }
    }

    public static void broadcast(Section section, PlaceholderReplacer replacer) {
        if (section == null) return;
        if (!section.contains("message")) return;
        if (section.isList("message")) {
            section.getOptionalStringList("message").ifPresent(strings -> broadcast(strings, null, replacer));
            return;
        }
        section.getOptionalString("message").ifPresent(s -> broadcast(s, null, replacer));
    }

    public static void broadcast(@Nullable String message, @Nullable String geyser, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;
        if (message == null) {
            broadcast(null, List.of(geyser), placeholderReplacer);
            return;
        }
        if (geyser == null) {
            broadcast(List.of(message), null, placeholderReplacer);
            return;
        }
        broadcast(List.of(message), List.of(geyser), placeholderReplacer);
    }

    public static void sendMessage(YamlDocument configuration, CommandSender player, String path, PlaceholderReplacer placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        configuration.getOptionalString(path + ".message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }

    public static void sendMessage(Section section, CommandSender player, PlaceholderReplacer placeholderReplacer) {
        if (section == null) return;
        if (!section.contains("message")) return;

        if (section.isList("message")) {
            section.getOptionalStringList("message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        section.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }

    public static void sendMessage(@Nullable String message, @Nullable String geyser, CommandSender player, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;
        if (message == null) {
            sendMessage(null, List.of(geyser), player, placeholderReplacer);
            return;
        }
        if (geyser == null) {
            sendMessage(List.of(message), null, player, placeholderReplacer);
            return;
        }
        sendMessage(List.of(message), List.of(geyser), player, placeholderReplacer);
    }

    public static void sendMessage(@Nullable List<String> message, @Nullable List<String> geyser, CommandSender player, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (geyser != null) {
            if (placeholderReplacer != null) {
                for (String s : geyser) {
                    player.sendMessage(MineDown.parse(parsePlaceholders(s, placeholderReplacer, null)));
                }
                return;
            }
            for (String s : geyser) {
                player.sendMessage(MineDown.parse(s));
            }
        }

        if (message != null) {
            if (placeholderReplacer != null) {
                for (String s : message) {
                    player.sendMessage(MineDown.parse(parsePlaceholders(s, placeholderReplacer, null)));
                }
                return;
            }
            for (String s : message) {
                player.sendMessage(MineDown.parse(s));
            }
        }
    }

    public static void sendMessage(@Nullable String message, @Nullable String geyser, Player player, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;
        if (message == null) {
            sendMessage(null, List.of(geyser), player, placeholderReplacer);
            return;
        }
        if (geyser == null) {
            sendMessage(List.of(message), null, player, placeholderReplacer);
            return;
        }
        sendMessage(List.of(message), List.of(geyser), player, placeholderReplacer);
    }

    public static void sendMessage(@Nullable List<String> message, @Nullable List<String> geyser, Player player, PlaceholderReplacer placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (geyser != null) {
            if (placeholderReplacer != null) {
                for (String s : geyser) {
                    player.sendMessage(MineDown.parse(parsePlaceholders(s, placeholderReplacer, player)));
                }
                return;
            }
            for (String s : geyser) {
                player.sendMessage(MineDown.parse(s));
            }
        }

        if (message != null) {
            if (placeholderReplacer != null) {
                for (String s : message) {
                    player.sendMessage(MineDown.parse(parsePlaceholders(s, placeholderReplacer, player)));
                }
                return;
            }
            for (String s : message) {
                player.sendMessage(MineDown.parse(s));
            }
        }
    }

    public static void sendMessage(YamlDocument configuration, Player player, String path, PlaceholderReplacer placeholderReplacer) {
        configuration.getOptionalSection(path).ifPresent(section -> sendMessage(section, player, placeholderReplacer));
    }

    public static void sendMessage(Section sec, Player player, PlaceholderReplacer placeholderReplacer) {

        sec.getOptionalSection("sound").ifPresent(section ->
                player.playSound(player.getLocation(), section.getString("id", "minecraft:block.amethyst_block.hit"),
                        section.getFloat("pitch", 1.0f),
                        section.getFloat("volume", 1.0f))
        );

        sec.getOptionalSection("title").ifPresent(section -> {

            if (sec.getSection("geyser-title", null) != null && GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                return;
            }

            Title.Times times = Title.Times.times(Ticks.duration(section.getInt("fadeIn", 10)),
                    Ticks.duration(section.getInt("stay", 70)),
                    Ticks.duration(section.getInt("fadeOut", 20)));
            Title title;

            if (placeholderReplacer == null) {
                title = Title.title(MineDown.parse(section.getString("title", "")),
                        MineDown.parse(section.getString("subtitle", "")), times);
            } else {
                title = Title.title(MineDown.parse(parsePlaceholders(section.getString("title", ""), placeholderReplacer, player)),
                        MineDown.parse(parsePlaceholders(section.getString("subtitle", ""), placeholderReplacer, player)), times);
            }

            player.showTitle(title);
        });

        sec.getOptionalSection("geyser-title").ifPresent(section -> {
            if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                Title.Times times = Title.Times.times(Ticks.duration(section.getInt("fadeIn", 10)),
                        Ticks.duration(section.getInt("stay", 70)),
                        Ticks.duration(section.getInt("fadeOut", 20)));

                Title title;

                if (placeholderReplacer == null) {
                    title = Title.title(MineDown.parse(section.getString("title", "")), MineDown.parse(section.getString("subtitle", "")), times);
                } else {
                    title = Title.title(MineDown.parse(parsePlaceholders(section.getString("title", ""), placeholderReplacer, player)), MineDown.parse(parsePlaceholders(section.getString("subtitle", ""), placeholderReplacer, player)), times);
                }

                player.showTitle(title);
            }
        });

        sec.getOptionalString("actionbar").ifPresent(message -> {

            if (sec.getString("geyser-actionbar", null) != null && GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                return;
            }

            if (placeholderReplacer == null) {
                player.sendActionBar(MineDown.parse(message));
            } else {
                player.sendActionBar(MineDown.parse(parsePlaceholders(message, placeholderReplacer, player)));
            }
        });

        sec.getOptionalString("geyser-actionbar").ifPresent(message -> {
            if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                if (placeholderReplacer == null) {
                    player.sendActionBar(MineDown.parse(message));
                } else {
                    player.sendActionBar(MineDown.parse(parsePlaceholders(message, placeholderReplacer, player)));
                }
            }
        });

        if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId()) && sec.contains("geyser")) {
            if (sec.isList("geyser")) {
                sec.getOptionalStringList("geyser").ifPresent(strings -> {
                    sendMessage(null, strings, player, placeholderReplacer);
                });
                return;
            }
            if (placeholderReplacer != null) {
                sec.getOptionalString("geyser").ifPresent(s -> sendMessage(null, s, player, placeholderReplacer));
                return;
            }
            sec.getOptionalString("geyser").ifPresent(s -> sendMessage(null, s, player, null));
            return;
        }

        if (sec.contains("message")) {
            if (sec.isList("message")) {
                sec.getOptionalStringList("message").ifPresent(strings -> {
                    sendMessage(strings, null, player, placeholderReplacer);
                });
                return;
            }
            if (placeholderReplacer != null) {
                sec.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
                return;
            }
            sec.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, null));
        }
    }

    /**
     * @param text The text to parse
     * @param replacer The replacer to use
     * @param player The player to parse for
     * @return The parsed text
     * */
    public static String parsePlaceholders(String text, @Nullable PlaceholderReplacer replacer, Player player) {

        if (replacer == null) {
            if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                return PlaceholderAPI.setPlaceholders(player, text);
            }
            return text;
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return replacer.parse(PlaceholderAPI.setPlaceholders(player, text));
        }
        return replacer.parse(text);
    }

    /**
     * @param message The message to colorify
     * @return The colorified message
     * */
    public static String colorify(String message) {
        message = hexColor(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
    * @param message The messages to colorify
    * @return The colorified messages
    * */

    public static List<String> colorify(List<String> message) {
        message.replaceAll(StringUtilsPaper::colorify);
        return message;
    }

    /**
     * @param arg The argument to complete
     * @param completions The list of completions
     * @return The parsed message
     * */
    public static List<String> partialCompletion(String arg, List<String> completions) {
        List<String> completion = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, completions, completion);
        Collections.sort(completion);
        return completion;
    }

    /**
     * Just an alias for {@link #partialCompletion(String, List)} because I forget the order of the arguments
     * @param list The list of completions
     * @param arg The argument to complete
     * @return The list of completions
     * */
    public static List<String> partialCompletion(List<String> list, String arg) {
        return partialCompletion(arg, list);
    }

    /**
     * @param message The message to center
     * @return The centered message
     * */
    public static String centerMessage(String message) {
        if (message == null || message.equals("")) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else if (previousCode && isBold) {
                isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int CENTER_PX = 154;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    /**
     * This method will convert into the minedown hex formatting instead of the regular hex color formatting.
     * This is solely for my personal development uses, if you never use this method, I dont blame you.
     * @param startingTag is the starting tag that will be used to scan for the hex color.
     * @param endTag is the end tag that will be used to scan for the hex color. (if it's empty, it will scan the next 6 characters after the starting tag)
     * @param message is the message that will be scanned for the tag.
     * */
    public static String hexColorMineDown(String startingTag, String endTag, String message) {
        return scanTags(startingTag, endTag, message, 6, group -> "&#"
                + group.charAt(0) + group.charAt(1)
                + group.charAt(2) + group.charAt(3)
                + group.charAt(4) + group.charAt(5) + "&");
    }

    public static String hexColorMineDown(String message) {
        return hexColorMineDown("&#", "", message);
    }

    /*
    * @param startingTag is the starting tag that will be used to scan for the hex color.
    * @param endTag is the end tag that will be used to scan for the hex color. (if it's empty, it will scan the next 6 characters after the starting tag)
    * @param message is the message that will be scanned for the tag.
    * @param chatColor is the chat color code that will be used when generating the hex color.
    * @return the message with the hex color replaced with the chat color code.
    * */
    public static String hexColor(String startingTag, String endTag, String message, String chatColor) {
        return scanTags(startingTag, endTag, message, 6, group -> chatColor + "x"
                + chatColor + group.charAt(0) + chatColor + group.charAt(1)
                + chatColor + group.charAt(2) + chatColor + group.charAt(3)
                + chatColor + group.charAt(4) + chatColor + group.charAt(5));
    }

    /**
    * @param message is the message that will be scanned for the tag.
     * @param chatColor is the chat color code that will be used when generating the hex color.
    * */
    public static String hexColor(String message, String chatColor) {
        return hexColor("&#", "", message, chatColor);
    }

    /**
     * @param message is the message that will be scanned for the tag.
     * */
    public static String hexColor(String message) {
        return hexColor(message, ChatColor.COLOR_CHAR + "");
    }

    /**
     * #scanTags is a method that scans a string for a specific tag and returns the value of the same message after the function is run on the tag.
     * The code for this method is not the prettiest, but it runs ~ 75% faster than using the regex method you commonly see on the internet.
     *
     * @param message       is the message that will be scanned for the tag.
     * @param messageLength is the length of the message if needed. You can set it to -1 if you don't need it. You will need it if you don't have an end tag.
     * @param startTag      The start tag that will be scanned for. This is required for the scanning.
     * @param endTag        The end tag that will be scanned for. This is not required for the scanning. If not provided, you will need a message length.
     * @param function      The function that will be run on the tag.
     * @return The message after the function is run on the tag.
     */
    public static String scanTags(String startTag, String endTag, String message, int messageLength, Function<String, String> function) {
        // initialize the character arrays
        char[] chars = message.toCharArray();
        char[] startTagChars = startTag.toCharArray();
        char[] endTagChars = endTag.toCharArray();

        if (startTagChars.length == 0) { // If the start tag is not provided, return the message
            return message;
        }

        if (endTagChars.length == 0 && messageLength == -1) { // If the end tag is not provided and the message length is not provided, return the message
            return message;
        }

        StringBuilder builder = new StringBuilder();
        boolean found = false;
        StringBuilder tag = new StringBuilder();

        for (int i = 0; i < chars.length; i++) { // Loop through the characters

            if (chars[i] == startTagChars[0]) { // If the first char of the start tag is found
                boolean foundStart = true;
                for (int j = 1; j < startTagChars.length; j++) { // Check if the rest of the start tag is found
                    if (chars[i + j] != startTagChars[j]) {
                        foundStart = false;
                        break;
                    }
                }
                if (foundStart) {
                    found = true;
                    i += startTagChars.length;
                }
            }
            if (!found) { // If the start tag is not found, just add the char to the builder
                builder.append(chars[i]);
                continue;
            }

            if (endTagChars.length == 0) { // If there is no end tag, just check the length of the tag
                if (tag.length() == messageLength) {
                    builder.append(function.apply(tag.toString()));
                    tag = new StringBuilder();
                    found = false;
                    builder.append(chars[i]);
                }
            } else if (chars[i] == endTagChars[0]) { // If the first char of the end tag is found
                boolean foundEnd = true;
                for (int j = 1; j < endTagChars.length; j++) { // Check if the rest of the end tag is found
                    if (chars[i + j] != endTagChars[j]) {
                        foundEnd = false;
                        break;
                    }
                }
                if (foundEnd) { // If the end tag is found, run the function on the tag and reset the tag
                    found = false;
                    i += endTagChars.length;

                    builder.append(function.apply(tag.toString()));
                    tag = new StringBuilder();
                }
            }
            if (found) { // If the start tag is found, add the char to the tag
                tag.append(chars[i]);
            }
        }

        return builder.toString();
    }
}
