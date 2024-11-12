package wtf.casper.amethyst.paper.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.utils.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class StringUtilsPaper extends StringUtils {

    private final static LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    private StringUtilsPaper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Expected input format:
     * <pre>
     * {@code
     * <message-id>:
     *   actionbar: <actionbar string>
     *   geyser-actionbar: <actionbar string for bedrock players only>
     *   message: <message string or string list>
     *   geyser: <message string or string list for bedrock players only>
     *   title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   geyser-title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   sound:
     *    id: <sound string id> (default: minecraft:block.amethyst_block.hit)
     *    volume: <sound volume> (default: 1.0)
     *    pitch: <sound pitch> (default: 1.0)
     * }
     * </pre>
     *
     * @param configuration       The configuration to get the message from
     * @param path                The path to the message
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void broadcast(YamlDocument configuration, String path, Placeholders placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> broadcast(strings, null, placeholderReplacer));
            return;
        }

        configuration.getOptionalString(path + ".message").ifPresent(s -> broadcast(s, null, placeholderReplacer));
    }

    /**
     * @param message             The message to broadcast
     * @param geyser              The geyser alternative message to broadcast
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void broadcast(@Nullable List<String> message, @Nullable List<String> geyser, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(message, geyser, player, placeholderReplacer);
        }
        for (String s : message) {
            Bukkit.getConsoleSender().sendMessage(parse(s, null, placeholderReplacer));
        }
    }

    public static void broadcast(Section section, Placeholders replacer) {
        if (section == null) return;
        if (!section.contains("message")) return;
        if (section.isList("message")) {
            section.getOptionalStringList("message").ifPresent(strings -> broadcast(strings, null, replacer));
            return;
        }
        section.getOptionalString("message").ifPresent(s -> broadcast(s, null, replacer));
    }

    /**
     * @param message             The message to broadcast
     * @param geyser              The geyser alternative message to broadcast
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void broadcast(@Nullable String message, @Nullable String geyser, Placeholders placeholderReplacer) {
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

    /**
     * Expected input format:
     * <pre>
     * {@code
     * <message-id>:
     *   actionbar: <actionbar string>
     *   geyser-actionbar: <actionbar string for bedrock players only>
     *   message: <message string or string list>
     *   geyser: <message string or string list for bedrock players only>
     *   title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   geyser-title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   sound:
     *    id: <sound string id> (default: minecraft:block.amethyst_block.hit)
     *    volume: <sound volume> (default: 1.0)
     *    pitch: <sound pitch> (default: 1.0)
     * }
     * </pre>
     *
     * @param configuration       The configuration to get the message from
     * @param player              The player to send the message to
     * @param path                The path to the message
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(YamlDocument configuration, CommandSender player, String path, Placeholders placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        configuration.getOptionalString(path + ".message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }

    /**
     * Expected input format:
     * <pre>
     * {@code
     * <message-id>:
     *   actionbar: <actionbar string>
     *   geyser-actionbar: <actionbar string for bedrock players only>
     *   message: <message string or string list>
     *   geyser: <message string or string list for bedrock players only>
     *   title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   geyser-title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   sound:
     *    id: <sound string id> (default: minecraft:block.amethyst_block.hit)
     *    volume: <sound volume> (default: 1.0)
     *    pitch: <sound pitch> (default: 1.0)
     * }
     * </pre>
     *
     * @param section             The section that is being parsed for message data
     * @param player              The player to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */

    public static void sendMessage(Section section, CommandSender player, Placeholders placeholderReplacer) {
        if (section == null) return;
        if (!section.contains("message")) return;

        if (section.isList("message")) {
            section.getOptionalStringList("message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        section.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }


    /**
     * Will not parse placeholderapi placeholders at all
     *
     * @param message             List of strings to parse & send to command sender
     * @param geyser              The list of geyser alternative messages to parse & send to command sender, if null it will default to java edition messsages
     * @param sender              The command sender to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(@Nullable String message, @Nullable String geyser, CommandSender sender, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (message == null) {
            sendMessage(null, List.of(geyser), sender, placeholderReplacer);
            return;
        }
        if (geyser == null) {
            sendMessage(List.of(message), null, sender, placeholderReplacer);
            return;
        }
        sendMessage(List.of(message), List.of(geyser), sender, placeholderReplacer);
    }

    /**
     * Will not parse placeholderapi placeholders at all
     *
     * @param message             List of strings to parse & send to command sender
     * @param geyser              The list of geyser alternative messages to parse & send to command sender, if null it will default to java edition messsages
     * @param sender              The command sender to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(@Nullable List<String> message, @Nullable List<String> geyser, CommandSender sender, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (geyser != null) {
            for (String s : geyser) {
                sender.sendMessage(parse(s, null, placeholderReplacer));
            }
            return;
        }

        for (String s : message) {
            sender.sendMessage(parse(s, null, placeholderReplacer));
        }
    }

    /**
     * @param message             String to parse & send to player
     * @param geyser              String of geyser alternative message to parse & send to player, if null it will default to java edition messsage
     * @param player              The player to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(@Nullable String message, @Nullable String geyser, Player player, Placeholders placeholderReplacer) {
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

    /**
     * @param message             List of strings to parse & send to player
     * @param geyser              The list of geyser alternative messages to parse & send to player, if null it will default to java edition messsages
     * @param player              The player to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(@Nullable List<String> message, @Nullable List<String> geyser, Player player, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (geyser != null) {
            for (String s : geyser) {
                player.sendMessage(parse(s, player, placeholderReplacer));
            }
            return;
        }

        for (String s : message) {
            player.sendMessage(parse(s, player, placeholderReplacer));
        }
    }

    /**
     * Expected input format:
     * <pre>
     * {@code
     * <message-id>:
     *   actionbar: <actionbar string>
     *   geyser-actionbar: <actionbar string for bedrock players only>
     *   message: <message string or string list>
     *   geyser: <message string or string list for bedrock players only>
     *   title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   geyser-title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   sound:
     *    id: <sound string id> (default: minecraft:block.amethyst_block.hit)
     *    volume: <sound volume> (default: 1.0)
     *    pitch: <sound pitch> (default: 1.0)
     * }
     * </pre>
     *
     * @param configuration       The configuration to get the message from
     * @param player              The player to send the message to
     * @param path                The path to the message
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */

    public static void sendMessage(YamlDocument configuration, @NotNull Player player, String path, @Nullable Placeholders placeholderReplacer) {
        configuration.getOptionalSection(path).ifPresent(section -> sendMessage(section, player, placeholderReplacer));
    }

    /**
     * Expected input format:
     * <pre>
     * {@code
     * <message-id>:
     *   actionbar: <actionbar string>
     *   geyser-actionbar: <actionbar string for bedrock players only>
     *   message: <message string or string list>
     *   geyser: <message string or string list for bedrock players only>
     *   title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   geyser-title:
     *     title: <title string> (optional, requires subtitle to be set if not set)
     *     subtitle: <subtitle string> (optional, requires title to be set if not set)
     *     fadeIn: <fade in time> (default: 10)
     *     stay: <stay time> (default: 70)
     *     fadeOut: <fade out time> (default: 20)
     *   sound:
     *    id: <sound string id> (default: minecraft:block.amethyst_block.hit)
     *    volume: <sound volume> (default: 1.0)
     *    pitch: <sound pitch> (default: 1.0)
     * }
     * </pre>
     *
     * @param sec                 The section that is being parsed for message data
     * @param player              The player to send the message to
     * @param placeholderReplacer The placeholder replacer to use, if null it will not replace any placeholders
     */
    public static void sendMessage(Section sec, @NotNull Player player, @Nullable Placeholders placeholderReplacer) {
        sec.getOptionalSection("sound").ifPresent(section ->
                player.playSound(
                        player.getLocation(),
                        section.getString("id", "minecraft:block.amethyst_block.hit"),
                        SoundCategory.valueOf(section.getString("category", "master").toUpperCase()),
                        section.getFloat("pitch", 1.0f),
                        section.getFloat("volume", 1.0f)
                )
        );

        sec.getOptionalSection("title").ifPresent(section -> {
            if (sec.getSection("geyser-title", null) != null
                    && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                return;
            }

            Title.Times times = Title.Times.times(
                    Ticks.duration(section.getInt("fadeIn", 10)),
                    Ticks.duration(section.getInt("stay", 70)),
                    Ticks.duration(section.getInt("fadeOut", 20))
            );

            Title title = Title.title(
                    LEGACY_COMPONENT_SERIALIZER.deserialize(parse(section.getString("title"), player, placeholderReplacer)),
                    LEGACY_COMPONENT_SERIALIZER.deserialize(parse(section.getString("subtitle"), player, placeholderReplacer)),
                    times
            );

            player.showTitle(title);
        });

        sec.getOptionalSection("geyser-title").ifPresent(section -> {
            if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                Title.Times times = Title.Times.times(
                        Ticks.duration(section.getInt("fadeIn", 10)),
                        Ticks.duration(section.getInt("stay", 70)),
                        Ticks.duration(section.getInt("fadeOut", 20))
                );

                Title title = Title.title(
                        LEGACY_COMPONENT_SERIALIZER.deserialize(parse(section.getString("title"), player, placeholderReplacer)),
                        LEGACY_COMPONENT_SERIALIZER.deserialize(parse(section.getString("subtitle"), player, placeholderReplacer)),
                        times
                );

                player.showTitle(title);
            }
        });

        sec.getOptionalString("actionbar").ifPresent(message -> {
            if (sec.getString("geyser-actionbar", null) != null && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                return;
            }

            player.sendActionBar(parse(message, player, placeholderReplacer));
        });

        sec.getOptionalString("geyser-actionbar").ifPresent(message -> {
            if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId())) {
                player.sendActionBar(parse(message, player, placeholderReplacer));
            }
        });

        if (GeyserUtils.isFloodgateEnabled() && GeyserUtils.isUserBedrock(player.getUniqueId()) && sec.contains("geyser")) {
            if (sec.isList("geyser")) {
                sec.getOptionalStringList("geyser").ifPresent(strings -> {
                    sendMessage(null, strings, player, placeholderReplacer);
                });
                return;
            }
            sec.getOptionalString("geyser").ifPresent(s -> sendMessage(null, s, player, placeholderReplacer));
            return;
        }

        if (sec.contains("message")) {
            if (sec.isList("message")) {
                sec.getOptionalStringList("message").ifPresent(strings -> {
                    sendMessage(strings, null, player, placeholderReplacer);
                });
                return;
            }
            sec.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
        }
    }

    private static String parse(String message, Player player, Placeholders placeholderReplacer) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { // always parse papi first because when given user input, users can input sensitive placeholders
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        if (placeholderReplacer != null) {
            message = placeholderReplacer.parse(message);
        }
        return HexUtils.colorify(message);
    }


    /**
     * @param current           The current progress
     * @param max               The max progress
     * @param totalBars         The total amount of bars
     * @param symbol            The symbol to use
     * @param completedColor    The color of the completed bars
     * @param notCompletedColor The color of the not completed bars
     */
    public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return completedColor + symbol.repeat(Math.max(0, progressBars)) + notCompletedColor + symbol.repeat(Math.max(0, totalBars - progressBars));
    }

    /**
     * @param arg         The argument to complete
     * @param completions The list of completions
     * @return The parsed message
     */
    public static List<String> partialCompletion(String arg, List<String> completions) {
        List<String> completion = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, completions, completion);
        Collections.sort(completion);
        return completion;
    }

    /**
     * Just an alias for {@link #partialCompletion(String, List)} because I forget the order of the arguments sometimes
     *
     * @param list The list of completions
     * @param arg  The argument to complete
     * @return The list of completions
     */
    public static List<String> partialCompletion(List<String> list, String arg) {
        return partialCompletion(arg, list);
    }

    /**
     * @param component The component to center
     * @return The centered component
     */
    public static Component centerMessage(Component component) {
        return Component.text(centerMessageSpaces(getContent(component))).append(component);
    }

    /**
     * @param message The message to center
     * @return The centered message
     */
    public static String centerMessage(String message) {
        return centerMessageSpaces(message) + message;
    }

    /**
     * @param component The component to center
     * @return The centered component
     */
    public static Component centerMessageSpaces(Component component) {
        return Component.text(centerMessageSpaces(getContent(component)));
    }

    /**
     * @param message The message to center
     * @return The spaces needed for the message
     */
    public static String centerMessageSpaces(String message) {
        return centerMessageSpaces(message, 0);
    }

    /**
     * @param message      The message to center
     * @param centerOffset The offset to center the message
     * @return The spaces needed for the message
     */
    public static String centerMessageSpaces(String message, int centerOffset) {
        if (message == null || message.isEmpty()) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        boolean isHex = false;
        int hexCount = 0;

        for (char c : message.toCharArray()) {
            if (c == ChatColor.COLOR_CHAR) {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L'; // &l
                isHex = c == 'x' || c == 'X'; // &x&r&r&g&g&b&b
                hexCount = isHex ? 12 : 0; // 14 char for hex codes but we are skipping the two one
            } else if (previousCode && isBold) {
                isBold = false;
            } else if (previousCode && isHex) {
                hexCount--;
                if (hexCount == 0) {
                    isHex = false;
                }
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int CENTER_PX = 154 + centerOffset;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }

    /**
     * @param component The component to get the content of (keeps bold intact)
     * @return The content of the component
     */
    public static String getContent(Component component) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(component);
    }

}
