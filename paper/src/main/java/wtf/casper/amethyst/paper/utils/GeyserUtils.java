package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Optional;
import java.util.UUID;

public class GeyserUtils {

    public static boolean isFloodgateEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("floodgate");
    }

    public static String getName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public static Optional<UUID> getUUID(String name) {
        if (GeyserUtils.isFloodgateEnabled()) {
            return Optional.of(FloodgateApi.getInstance().getUuidFor(name).join());
        }

        UUID uniqueId = Bukkit.getOfflinePlayer(name).getUniqueId();
        return Optional.of(uniqueId);
    }

    public static boolean isUserBedrock(UUID uuid) {
        if (GeyserUtils.isFloodgateEnabled()) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }

        return uuid.getMostSignificantBits() == 0; // fallback
    }

}
