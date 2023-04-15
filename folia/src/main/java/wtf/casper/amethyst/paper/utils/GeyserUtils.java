package wtf.casper.amethyst.paper.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.core.storage.FieldStorage;
import wtf.casper.amethyst.core.storage.id.Id;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.amethyst.paper.geyser.GeyserJsonStorage;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class GeyserUtils extends AmethystListener<AmethystPlugin> {

    @Getter
    private static FieldStorage<UUID, GeyserPlayer> geyserStorage;

    public GeyserUtils(AmethystPlugin plugin) {
        super(plugin);
        GeyserUtils.geyserStorage = new GeyserJsonStorage(new File(plugin.getDataFolder(), "geyser.json"), GeyserPlayer.class);
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
        if (isFloodgateEnabled() && isUserBedrock(uuid)) {
            GeyserPlayer join = getGeyserStorage().get(uuid).join();
            if (join != null && join.getName() != null) {
                return join.getName();
            }
            return Bukkit.getOfflinePlayer(uuid).getName();
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
                return Optional.of(geyserStorage.getFirst("name", name).join().getUuid());
            }
        }
        if (uuid == null) {
            return Optional.empty();
        }
        return Optional.of(uuid);
    }

    public static boolean isUserBedrock(UUID uuid) {
        return uuid.getMostSignificantBits() == 0;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isUserBedrock(event.getPlayer().getUniqueId())) {
            return;
        }

        geyserStorage.getOrDefault(event.getPlayer().getUniqueId()).whenComplete((geyserPlayer, throwable) -> {
            geyserPlayer.setName(event.getPlayer().getName());
        });
    }

    @Getter
    public static class GeyserPlayer {
        @Id
        private final UUID uuid;
        @Setter
        private String name;

        public GeyserPlayer(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }
    }
}
