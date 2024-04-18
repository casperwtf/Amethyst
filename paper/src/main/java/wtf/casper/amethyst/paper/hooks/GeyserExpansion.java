package wtf.casper.amethyst.paper.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.paper.utils.GeyserUtils;

import java.util.Locale;

public class GeyserExpansion extends PlaceholderExpansion implements Relational {

    @Override
    public @NotNull String getIdentifier() {
        return "geysercheck";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Casper";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");

        if (args.length == 0) return "";
        if (!GeyserUtils.isFloodgateEnabled()) return "";

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "isbedrock" -> {
                return GeyserUtils.isUserBedrock(player.getUniqueId()) ? "true" : "false";
            }
            case "replace" -> {
                if (GeyserUtils.isUserBedrock(player.getUniqueId())) {
                    return replaceFakePlaceholders(player, args[2]);
                }
                return replaceFakePlaceholders(player, args[1]);
            }
        }
        return "";
    }

    @Override
    public String onPlaceholderRequest(Player one, Player two, String params) {
        //arg 1 = isjava
        //arg 2 = isbedrock
        String[] args = params.split("(?<!\\\\)_"); // match "_" but not "\_"

        if (args.length != 3) return "";
        if (!GeyserUtils.isFloodgateEnabled()) return "";

        if (args[0].toLowerCase(Locale.ROOT).equals("replace")) {
            // if both are bedrock
            if (GeyserUtils.isUserBedrock(one.getUniqueId()) && GeyserUtils.isUserBedrock(two.getUniqueId())) {
                return replaceFakePlaceholders(one, args[2]);
            }

            // if one is bedrock and two is java
            if (GeyserUtils.isUserBedrock(one.getUniqueId()) && !GeyserUtils.isUserBedrock(two.getUniqueId())) {
                return replaceFakePlaceholders(one, args[2]);
            }

            // if one is java and two is bedrock
            if (!GeyserUtils.isUserBedrock(one.getUniqueId()) && GeyserUtils.isUserBedrock(two.getUniqueId())) {
                return replaceFakePlaceholders(one, args[1]);
            }

            // if both are java
            if (!GeyserUtils.isUserBedrock(one.getUniqueId()) && !GeyserUtils.isUserBedrock(two.getUniqueId())) {
                return replaceFakePlaceholders(one, args[1]);
            }
        }

        return "";
    }

    private String replaceFakePlaceholders(Player player, String params) {
        String[] args = params.split("_");

        if (args.length == 0) return "";
        if (!GeyserUtils.isFloodgateEnabled()) return "";

        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if (arg.startsWith("{") && arg.endsWith("}")) {
                String placeholder = "%" + arg.substring(1, arg.length() - 1) + "%";
                builder.append(PlaceholderAPI.setPlaceholders(player, placeholder));
                builder.append(" ");
            } else {
                builder.append(arg);
                builder.append(" ");
            }
        }

        return builder.toString().trim();
    }


}
