package wtf.casper.amethyst.paper.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import wtf.casper.amethyst.paper.AmethystPaper;

public class BungeeUtil {

    /**
     * Register outgoing channel
     *
     * @param plugin  Plugin instance to use
     * @param channel Channel to register
     */
    public static void registerOutChannel(JavaPlugin plugin, String channel) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
    }

    /**
     * Register incoming channel
     *
     * @param plugin   Plugin instance to use
     * @param channel  Channel to register
     * @param listener Listener to register
     */
    public static void registerInChannel(JavaPlugin plugin, String channel, PluginMessageListener listener) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, listener);
    }

    /**
     * Register BungeeCord channel
     *
     * @param plugin Plugin instance to use
     */
    public static void registerBungeeCord(JavaPlugin plugin) {
        AmethystPaper.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    /**
     * Register BungeeCord channel
     */
    public static void registerBungeeCord() {
        AmethystPaper.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(ServerUtils.getCallingPlugin(), "BungeeCord");
    }

    /**
     * Send player to server
     *
     * @param player Player to send
     * @param server Server to send to
     */
    public static void sendPlayerToServer(Player player, String server) {
        JavaPlugin plugin = ServerUtils.getCallingPlugin();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Send bungee message to send player to server
     *
     * @param player Player to send
     * @param plugin Plugin instance to use
     * @param server Server to send to
     */
    public static void sendPlayerToServer(Player player, JavaPlugin plugin, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}
