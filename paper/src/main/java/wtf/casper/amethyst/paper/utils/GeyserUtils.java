package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GeyserUtils {

    @Nullable
    public static FloodgateApi floodgate() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Floodgate")) {
            return FloodgateApi.getInstance();
        }
        return null;
    }

    public static boolean isFloodgateEnabled() {
        return floodgate() != null;
    }

    public static String getName(UUID uuid) {
        if (isFloodgateEnabled() && isUserBedrock(uuid)) {
            return floodgate().getPlayer(uuid).getUsername();
        }
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public static Optional<UUID> getUUID(String name) {
        UUID uuid = null;
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
        if (player != null) {
            uuid = player.getUniqueId();
        } else {
            if (isFloodgateEnabled()) {
                uuid = floodgate().getUuidFor(name).join();
            }
            if (uuid == null) {
                uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
            }
        }
        return Optional.of(uuid);
    }

    public static boolean isUserBedrock(UUID uuid) {
        return uuid.getMostSignificantBits() == 0;
    }
}
