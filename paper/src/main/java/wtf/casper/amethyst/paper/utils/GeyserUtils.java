package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Optional;
import java.util.UUID;

public class GeyserUtils {

    private GeyserUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check if Floodgate is enabled
     * @return true if Floodgate is enabled
     */
    public static boolean isFloodgateEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("floodgate");
    }

    /**
     * Get the name of a player by their UUID
     * @param uuid UUID of the player
     * @param uuid
     * @return
     */
    public static String getName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    /**
     * Get the UUID of a player by their name
     * @param name Name of the player
     * @return UUID of the player
     */
    public static Optional<UUID> getUUID(String name) {
        if (GeyserUtils.isFloodgateEnabled()) {
            return Optional.of(FloodgateApi.getInstance().getUuidFor(name).join());
        }

        UUID uniqueId = Bukkit.getOfflinePlayer(name).getUniqueId();
        return Optional.of(uniqueId);
    }

    /**
     * Check if a player is a Bedrock player (Floodgate)
     * @param uuid UUID of the player
     * @return true if the player is a Bedrock player
     */
    public static boolean isUserBedrock(UUID uuid) {
        if (GeyserUtils.isFloodgateEnabled()) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        }

        return uuid.getMostSignificantBits() == 0; // fallback
    }

}
