package wtf.casper.amethyst.core.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {

    private final static DecimalFormat formatter = new DecimalFormat("#,###.#");

    /**
     * @param input    the string to check
     * @param enumType the enum type to check against
     *                 <p>
     *                 Checks if a string is a valid enum value
     */
    public static boolean validateEnum(String input, Class<? extends Enum> enumType) {
        if (enumType == null) {
            return false;
        }
        try {
            Enum<?> en = Enum.valueOf(enumType, input);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * @param enumType the enum type to check against
     * @return a list of all enum names
     */
    public static List<String> getEnumNames(Class<? extends Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
    }

    /**
     * @param string the string to capitalize
     *               <p>
     *               Capitalizes the first letter of each word in a string
     */
    public static String capitalizeString(String string) {
        StringBuilder output = new StringBuilder();
        for (String s : string.split(" ")) {
            output.append(" ")
                    .append(s.substring(0, 1).toUpperCase(Locale.ROOT))
                    .append(s.substring(1).toLowerCase(Locale.ROOT));
        }
        return output.toString().trim();
    }

    /**
     * @param enumValue the enum value to format
     *                  <p>
     *                  Formats an enum value to be human-readable
     *                  Example: "MY_ENUM_VALUE" -> "My Enum Value"
     */
    public static String formatEnum(String enumValue) {
        if (enumValue == null) return null;
        return capitalizeString(enumValue.replaceAll("_", " "));
    }

    /**
     * @param enumValue the enum value to format
     *                  <p>
     *                  Formats an enum value to be human-readable
     *                  Example: "MY_ENUM_VALUE" -> "My Enum Value"
     */
    public static String formatEnum(Enum<?> enumValue) {
        if (enumValue == null) return null;
        return capitalizeString(enumValue.toString().replaceAll("_", " "));
    }

    /**
     * @param current   The current progress
     * @param max       The maximum progress
     * @param totalBars total bars to display
     * @param symbol    the symbol to use for the bar
     * @return a string of the progress bar
     */
    public static String getProgressBar(int current, int max, int totalBars, String symbol) {

        if (max == 0) {
            current = 1;
            max = 1;
        }

        if (current > max) {
            current = max;
        }

        float percent = (float) current / max;

        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);

        return String.valueOf(symbol).repeat(Math.max(0, progressBars)) + String.valueOf(symbol).repeat(Math.max(0, leftOver));
    }

    /**
     * @param time1 The first time (This time should be the latest time)
     * @param time2 The second time (This time should be the earliest time)
     * @return The time formatted into a string to go up to the days
     */
    public static String findDifferenceDays(long time1, long time2) {

        long differenceInTime = time1 - time2;

        return TimeUnit.MILLISECONDS.toDays(differenceInTime) % 24 + "d, "
                + TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24 + "h, "
                + TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60 + "m, "
                + TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60 + "s";
    }

    /**
     * @param time1 The first time (This time should be the latest time)
     * @param time2 The second time (This time should be the earliest time)
     * @return The time formatted into a string to go up to the hours
     */
    public static String findDifferenceHour(long time1, long time2) {

        long differenceInTime = time1 - time2;

        return TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24 + "h, "
                + TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60 + "m, "
                + TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60 + "s";
    }

    /**
     * @param time1 The first time (This time should be latest time)
     * @param time2 The second time (This time should be earliest time)
     * @return The time formatted into a string to go up to the minutes
     */
    public static String findDifferenceMinutes(long time1, long time2) {

        long differenceInTime = time1 - time2;

        return TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60 + "m, "
                + TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60 + "s";
    }

    /**
     * Runs ${@link StringUtils#findDifferenceMinutes(long, long)} and formats time2 to be the current time
     *
     * @param time1 This should be a time in the future
     * @return The time formatted into a string to go up to the minutes
     */
    public static String timeUntilMinutes(long time1) {
        return findDifferenceMinutes(time1, System.currentTimeMillis());
    }

    /**
     * Runs ${@link StringUtils#findDifferenceDays(long, long)} and formats time2 to be the current time
     *
     * @param time1 This should be a time in the future
     * @return The time formatted into a string to go up to the days
     */
    public static String timeUntilDays(long time1) {
        return findDifferenceDays(time1, System.currentTimeMillis());
    }

    /**
     * Runs ${@link StringUtils#findDifferenceHour(long, long)} and formats time2 to be the current time
     *
     * @param time1 This should be a time in the future
     * @return The time formatted into a string to go up to the hours
     */
    public static String timeUntilHours(long time1) {
        return findDifferenceHour(time1, System.currentTimeMillis());
    }

    /**
     * @param time1 This should be a time in the future
     * @return The time formatted into a string to go up to the days
     */
    public static String timeUntil(long time1) {

        long differenceInTime = time1 - System.currentTimeMillis();

        long days = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 24;
        long hours = TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60;

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            return "0s";
        }

        if (days == 0 && hours == 0 && minutes == 0) {
            return seconds + "s";
        }

        if (days == 0 && hours == 0) {
            return minutes + "m, " + seconds + "s";
        }

        if (days == 0) {
            return hours + "h, " + minutes + "m, " + seconds + "s";
        }

        return days + "d, " + hours + "h, " + minutes + "m, " + seconds + "s";
    }

    /**
     * @param number The number to format with commas
     * @return The number formatted with commas
     */
    public static String comma(int number) {
        return formatter.format(number);
    }

    /**
     * @param number The number to format with commas
     * @return The number formatted with commas
     */
    public static String comma(double number) {
        return formatter.format(number);
    }

    /**
     * @param number The number to abbreviate with K, M, B, T, etc.
     * @return The number formatted with K, M, B, T, etc.
     */
    public static String abbreviate(int number) {

        if (number >= 1000000000) {
            return String.format("%.2fB", number / 1000000000.0);
        }
        if (number >= 1000000) {
            return String.format("%.2fM", number / 1000000.0);
        }
        if (number >= 1000) {
            return String.format("%.2fK", number / 1000.0);
        }

        return String.valueOf(number);
    }

    /**
     * @param number The number to abbreviate with K, M, B, T, etc.
     * @return The number formatted with K, M, B, T, etc.
     */
    public static String abbreviate(double number) {

        if (number >= 1000000000) {
            return String.format("%.2fB", number / 1000000000.0);
        }
        if (number >= 1000000) {
            return String.format("%.2fM", number / 1000000.0);
        }
        if (number >= 1000) {
            return String.format("%.2fK", number / 1000.0);
        }

        return String.valueOf(number);
    }

    /**
     * @param input    The input to format
     * @param timeUnit The time unit to convert to
     * @return The time in the specified time unit
     * Example: "1w 2d 3h 4m 5s", TimeUnit.SECONDS -> 788645
     */
    public long timeFromString(String input, TimeUnit timeUnit) {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;

        String[] split = input.split(" ");
        for (String s : split) {
            if (s.endsWith("s")) {
                seconds = Integer.parseInt(s.substring(0, s.length() - 1));
            } else if (s.endsWith("m")) {
                minutes = Integer.parseInt(s.substring(0, s.length() - 1));
            } else if (s.endsWith("h")) {
                hours = Integer.parseInt(s.substring(0, s.length() - 1));
            } else if (s.endsWith("d")) {
                days = Integer.parseInt(s.substring(0, s.length() - 1));
            } else if (s.endsWith("w")) {
                weeks = Integer.parseInt(s.substring(0, s.length() - 1));
            }
        }

        return timeUnit.convert(((long) weeks * 7 * 24 * 60 * 60)
                + ((long) days * 24 * 60 * 60)
                + ((long) hours * 60 * 60)
                + (minutes * 60L)
                + seconds, TimeUnit.SECONDS);
    }

    /**
     * @param line       The line to wrap
     * @param lineLength The length of the line
     * @return The line wrapped to the specified length
     * The wrap is split with \n characters
     */
    public static String wrapLine(String line, int lineLength) {
        if (line.length() == 0) return "\n";
        if (line.length() <= lineLength) return line + "\n";
        String[] words = line.split(" ");
        StringBuilder allLines = new StringBuilder();
        StringBuilder trimmedLine = new StringBuilder();
        for (String word : words) {
            if (trimmedLine.length() + 1 + word.length() <= lineLength) {
                trimmedLine.append(word).append(" ");
            } else {
                allLines.append(trimmedLine).append("\n");
                trimmedLine = new StringBuilder();
                trimmedLine.append(word).append(" ");
            }
        }
        if (trimmedLine.length() > 0) {
            allLines.append(trimmedLine);
        }
        allLines.append("\n");
        return allLines.toString();
    }

    /**
     * @param text  The text to check
     * @param chars The characters to check for
     * @return True if the text only contains the characters in the char array
     */
    public static boolean onlyContains(String text, char[] chars) {
        // only contains characters found in the char array
        for (char c : text.toCharArray()) {
            boolean found = false;
            for (char c1 : chars) {
                if (c == c1) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param text The text to check
     * @return True if the text only contains alphanumeric characters
     */
    public static boolean isAlphaNumeric(String text) {
        return onlyContains(text, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray());
    }

    /**
     * @param string     The string to wrap
     * @param lineLength The length of the line
     * @param prefix     The prefix to add to each line
     * @param suffix     The suffix to add to each line
     */
    public static List<String> wrap(String string, int lineLength, String prefix, String suffix) {
        StringBuilder b = new StringBuilder();
        for (String line : string.split(Pattern.quote("\n"))) {
            b.append(wrapLine(line, lineLength));
        }

        List<String> output = new ArrayList<>();
        for (String s : Arrays.stream(b.toString().split("\n")).toList()) {
            output.add(prefix + s + suffix);
        }

        return output;
    }
}
