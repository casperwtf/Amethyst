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

    public static void broadcast(YamlDocument configuration, String path, Placeholders placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> broadcast(strings, null, placeholderReplacer));
            return;
        }

        configuration.getOptionalString(path + ".message").ifPresent(s -> broadcast(s, null, placeholderReplacer));
    }

    public static void broadcast(@Nullable List<String> message, @Nullable List<String> geyser, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(message, geyser, player, placeholderReplacer);
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

    public static void sendMessage(YamlDocument configuration, CommandSender player, String path, Placeholders placeholderReplacer) {
        if (!configuration.contains(path)) return;
        if (!configuration.contains(path + ".message")) return;

        if (configuration.isList(path + ".message")) {
            configuration.getOptionalStringList(path + ".message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        configuration.getOptionalString(path + ".message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }

    public static void sendMessage(Section section, CommandSender player, Placeholders placeholderReplacer) {
        if (section == null) return;
        if (!section.contains("message")) return;

        if (section.isList("message")) {
            section.getOptionalStringList("message").ifPresent(strings -> sendMessage(strings, null, player, placeholderReplacer));
            return;
        }
        section.getOptionalString("message").ifPresent(s -> sendMessage(s, null, player, placeholderReplacer));
    }

    public static void sendMessage(@Nullable String message, @Nullable String geyser, CommandSender player, Placeholders placeholderReplacer) {
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

    public static void sendMessage(@Nullable List<String> message, @Nullable List<String> geyser, CommandSender player, Placeholders placeholderReplacer) {
        if (message == null && geyser == null) return;

        if (geyser != null) {
            for (String s : geyser) {
                player.sendMessage(parse(s, null, placeholderReplacer));
            }
            return;
        }

        for (String s : message) {
            player.sendMessage(parse(s, null, placeholderReplacer));
        }
    }

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

    public static void sendMessage(YamlDocument configuration, @NotNull Player player, String path, @Nullable Placeholders placeholderReplacer) {
        configuration.getOptionalSection(path).ifPresent(section -> sendMessage(section, player, placeholderReplacer));
    }

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
        if (placeholderReplacer != null) {
            message = placeholderReplacer.parse(message);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
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
     * Just an alias for {@link #partialCompletion(String, List)} because I forget the order of the arguments
     *
     * @param list The list of completions
     * @param arg  The argument to complete
     * @return The list of completions
     */
    public static List<String> partialCompletion(List<String> list, String arg) {
        return partialCompletion(arg, list);
    }

    // just here for compatibility
    public static String colorify(String string) {
        return HexUtils.colorify(string);
    }

    // just here for compatibility
    public static List<String> colorify(List<String> list) {
        return HexUtils.colorify(list);
    }

    /**
     * @param message The message to center
     * @return The spaces needed for the message
     */
    public static String centerMessageSpaces(String message) {
        if (message == null || message.isEmpty()) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == ChatColor.COLOR_CHAR) {
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
