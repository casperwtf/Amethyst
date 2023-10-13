package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GeyserUtils extends AmethystListener<JavaPlugin> {

    public GeyserUtils(JavaPlugin plugin) {
        super(plugin);
    }

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
        return Bukkit.getOfflinePlayer(uuid).getName();
        //TODO: Implement this with geyser support

//        if (isFloodgateEnabled() && isUserBedrock(uuid)) {
//            GeyserPlayer join = getGeyserStorage().get(uuid).join();
//            if (join != null && join.getName() != null) {
//                return join.getName();
//            }
//            return Bukkit.getOfflinePlayer(uuid).getName();
//        }
//        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public static Optional<UUID> getUUID(String name) {

        //TODO: There is a better way to do this with geyser support

        if (GeyserUtils.isFloodgateEnabled()) {
            return Optional.of(GeyserUtils.floodgate().getUuidFor(name).join());
        }

        UUID uniqueId = Bukkit.getOfflinePlayer(name).getUniqueId();
        return Optional.ofNullable(uniqueId);
    }

    public static boolean isUserBedrock(UUID uuid) {
        if (GeyserUtils.isFloodgateEnabled()) {
            return floodgate().isFloodgatePlayer(uuid);
        }
        return uuid.getMostSignificantBits() == 0;
    }

}
