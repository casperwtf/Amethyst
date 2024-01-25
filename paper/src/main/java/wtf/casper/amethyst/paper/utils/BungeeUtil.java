package wtf.casper.amethyst.paper.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.AmethystPlugin;

public class BungeeUtil {

    public static void registerOutChannel(JavaPlugin plugin, String channel) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
    }

    public static void registerInChannel(JavaPlugin plugin, String channel, PluginMessageListener listener) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, listener);
    }

    public static void sendPlayerToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(AmethystPaper.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendPlayerToServer(Player player, AmethystPlugin plugin, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}
