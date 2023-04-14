package wtf.casper.amethyst.paper.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.paper.utils.GeyserUtils;

import java.util.Locale;

public class GeyserExpansion extends PlaceholderExpansion {

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

        player.getTargetBlockExact(5);

        if (args.length == 0) return "";
        if (GeyserUtils.floodgate() == null) return "";

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "isbedrock": {
                return GeyserUtils.isUserBedrock(player.getUniqueId()) ? "true" : "false";
            }
            case "replace": {
                if (GeyserUtils.isUserBedrock(player.getUniqueId())) {
                    return args[2];
                }
                return args[1];
            }
        }
        return "";
    }
}
