package wtf.casper.papertests;

import org.bukkit.ChatColor;
import wtf.casper.amethyst.paper.utils.StringUtilsPaper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorPerfTest implements Test{

    private final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");

    @Override
    public boolean test() {
        String color = "&#FFFFFF";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            StringUtilsPaper.hexColor(color+"T"+color+"E"+color+"S"+color+"T");
        }
        System.out.println("1 | Time taken: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            translateHexColorCodes(color+"T"+color+"E"+color+"S"+color+"T");
        }
        System.out.println("2 | Time taken: " + (System.currentTimeMillis() - start) + "ms");

        return true;
    }

    private String translateHexColorCodes(String message) {
        //Sourced from this post by imDaniX: https://github.com/SpigotMC/BungeeCord/pull/2883#issuecomment-653955600
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            char COLOR_CHAR = ChatColor.COLOR_CHAR;
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
