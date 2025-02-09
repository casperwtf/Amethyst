package wtf.casper.amethyst.paper.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import wtf.casper.amethyst.core.collections.Octree;
import wtf.casper.amethyst.paper.events.AsyncPlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerOctree implements Listener {

    private final Octree<Player> octree;
    private final Map<Player, Octree.Point3D> playerPositions;

    public PlayerOctree(Plugin plugin, Location center, double size) {
        if (size == 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        Octree.BoundingBox boundingBox = new Octree.BoundingBox(new Octree.Point3D(center.getBlockX(), center.getBlockY(), center.getBlockZ()), size/2);
        this.octree = new Octree<>(boundingBox);
        this.playerPositions = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getOnlinePlayers().forEach(this::addPlayer);
    }

    private Octree.Point3D toPoint3D(Location location) {
        return new Octree.Point3D(location.getX(), location.getY(), location.getZ());
    }

    public boolean addPlayer(Player player) {
        Octree.Point3D point = toPoint3D(player.getLocation());
        if (octree.insert(point, player)) {
            playerPositions.put(player, point);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        Octree.Point3D point = playerPositions.remove(player);
        if (point != null) {
            return octree.remove(point, player);
        }
        return false;
    }

    public void updatePlayerPosition(Player player) {
        removePlayer(player);
        addPlayer(player);
    }

    public List<Player> getPlayersInRange(Location location, double range) {
        Octree.Point3D point = toPoint3D(location);
        Octree.BoundingBox searchArea = new Octree.BoundingBox(point, range);
        return octree.queryRange(searchArea);
    }

    @EventHandler
    public void onPlayerMove(AsyncPlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.hasChangedBlock()) {
            updatePlayerPosition(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayer(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        addPlayer(player);
    }
}