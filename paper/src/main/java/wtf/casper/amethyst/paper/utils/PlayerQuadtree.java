package wtf.casper.amethyst.paper.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import wtf.casper.amethyst.core.collections.Octree;
import wtf.casper.amethyst.core.collections.Quadtree;
import wtf.casper.amethyst.paper.events.AsyncPlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerQuadtree implements Listener {

    private final Quadtree<Player> quadtree;
    private final Map<Player, Quadtree.Point2D> playerPositions;

    public PlayerQuadtree(Plugin plugin, Location center, double size) {
        if (size == 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        Quadtree.BoundingBox boundingBox = new Quadtree.BoundingBox(new Quadtree.Point2D(center.getBlockX(), center.getBlockZ()), size/2);
        this.quadtree = new Quadtree<>(boundingBox);
        this.playerPositions = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getOnlinePlayers().forEach(this::addPlayer);
    }

    private Quadtree.Point2D toPoint2D(Location location) {
        return new Quadtree.Point2D(location.getX(), location.getZ());
    }

    public boolean addPlayer(Player player) {
        Quadtree.Point2D point = toPoint2D(player.getLocation());
        if (quadtree.insert(point, player)) {
            playerPositions.put(player, point);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        Quadtree.Point2D point = playerPositions.remove(player);
        if (point != null) {
            return quadtree.remove(point, player);
        }
        return false;
    }

    public void updatePlayerPosition(Player player) {
        removePlayer(player);
        addPlayer(player);
    }

    public List<Player> getPlayersInRange(Location location, double range) {
        Quadtree.Point2D point = toPoint2D(location);
        return quadtree.queryRange(point, range);
    }

    @EventHandler
    public void onPlayerMove(AsyncPlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }

        Player player = event.getPlayer();
        Quadtree.Point2D oldPoint = playerPositions.get(player);
        Quadtree.Point2D newPoint = toPoint2D(player.getLocation());

        if (oldPoint != null && !oldPoint.equals(newPoint)) {
            playerPositions.put(player, newPoint);
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