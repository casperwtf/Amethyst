package wtf.casper.amethyst.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import gg.optimalgames.hologrambridge.HologramBridge;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.DiscordWebhook;
import wtf.casper.amethyst.core.utils.pastes.PasteProvider;
import wtf.casper.amethyst.core.utils.pastes.Pastebin;
import wtf.casper.amethyst.paper.hooks.GeyserExpansion;
import wtf.casper.amethyst.paper.hooks.combat.CombatManager;
import wtf.casper.amethyst.paper.hooks.economy.EconomyManager;
import wtf.casper.amethyst.paper.hooks.protection.ProtectionManager;
import wtf.casper.amethyst.paper.hooks.stacker.StackerManager;
import wtf.casper.amethyst.paper.hooks.vanish.VanishManager;
import wtf.casper.amethyst.paper.listeners.PluginListener;
import wtf.casper.amethyst.paper.serialized.SerializableItem;
import wtf.casper.amethyst.paper.serialized.SerializableItemTypeAdapter;
import wtf.casper.amethyst.paper.serialized.serializer.*;
import wtf.casper.amethyst.paper.tracker.PlayerTracker;
import wtf.casper.amethyst.paper.tracker.PlayerTrackerListener;
import wtf.casper.amethyst.paper.utils.ArmorstandUtils;
import wtf.casper.amethyst.paper.utils.GeneralUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;

public class AmethystPaper extends AmethystPlugin implements Listener {

    @Getter private static final Map<JavaPlugin, InventoryManager> inventoryManagers = new HashMap<>();
    @Getter private static Economy economy = null;
    @Getter private static AmethystPaper instance = null;
    @Getter private static Filter filter;
    @Getter private final NamespacedKey playerPlacedBlockKey = new NamespacedKey(this, "PLAYER_PLACED_BLOCK");

    @Override
    public void enable() {
        saveDefaultConfig();
        saveConfig();

        CustomBlockData.registerListener(this);

        instance = this;

        filter = record -> {

            if (record.getThrown() == null || record.getThrown().getCause() == null) {
                return true;
            }

            boolean isOurPlugin = false;
            for (StackTraceElement stackTraceElement : record.getThrown().getCause().getStackTrace()) {
                for (String packageName : getYamlConfig().getStringList("logged-packages")) {
                    if (stackTraceElement.getClassName().startsWith(packageName)) {
                        isOurPlugin = true;
                        break;
                    }
                }
            }

            if (!isOurPlugin) {
                return true;
            }

            File log = new File("logs" + File.separator + "latest.log");
            PasteProvider.paste(
                    "Server Version: " + Bukkit.getVersionMessage() + "\n" +
                            "Server Jar: " + GeneralUtils.getServerJar(log) + "\n" +
                            "Java Version: " + System.getProperty("java.version") + "\n\n\n" +
                            GeneralUtils.readLog(log)
            ).whenComplete((link, throwable) -> {
                if (throwable != null) {
                    AmethystLogger.error("Failed to paste log to pasting service");
                    throwable.printStackTrace();
                }
                if (link == null) {
                    AmethystLogger.log("", "", "An error was found in the console, please report this to the developer of the plugin that caused this error." +
                            " An error was found when generating an error link, please report that to the developer as-well." +
                            " Please send a spark report that has been running for at least 5 minutes as-well as the entirety of your server's" +
                            " latest.log file.", "", "");
                    return;
                }

                AmethystLogger.log("", "", "An error was found in the console, please report this to the developer of the plugin that caused this error." +
                        " Please forward this link to the developer. " + link, "", "");

                if (getYamlConfig().getBoolean("send-errors-to-discord")) {
                    DiscordWebhook webhook = new DiscordWebhook(getYamlConfig().getString("discord-webhook"));

                    StringBuilder stacktrace = new StringBuilder();
                    for (StackTraceElement stackTraceElement : record.getThrown().getCause().getStackTrace()) {
                        stacktrace.append(stackTraceElement.toString()).append("\n");
                    }

                    webhook.addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle("Error")
                            .setDescription("An error was found in the console, please report this to the developer of the plugin that caused this error." +
                                    " Please forward this link to the developer.\n" + stacktrace)
                            .addField("Link", link, false)
                            .setColor(Color.decode("#ff0000")));
                    try {
                        webhook.execute();
                    } catch (IOException ignored) {
                    }
                }
            });
            return true;
        };

        getLogger().setFilter(filter);
        Bukkit.getLogger().setFilter(filter);
        AmethystLogger.getLogger().setFilter(filter);

        new PluginListener(this);
        new ArmorstandUtils(this);

        setupConfigOptions();
        setupEconomy();

        AmethystCore.init();
        AmethystCore.registerTypeAdapter(SerializableItemTypeAdapter.class, new SerializableItemTypeAdapter());
        AmethystCore.registerTypeAdapter(Chunk.class, new ChunkSerializer());
        AmethystCore.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
        AmethystCore.registerTypeAdapter(Location.class, new LocationSerializer());
        AmethystCore.registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerSerializer());
        AmethystCore.registerTypeAdapter(World.class, new WorldSerializer());
        AmethystCore.registerTypeAdapter(SerializableItem.class, new SerializableItemSerializer());

        if (Bukkit.getPluginManager().isPluginEnabled("Floodgate") && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new GeyserExpansion().register();
        }

        new HologramBridge(this, true);

        EconomyManager.init();
        new CombatManager();
        new ProtectionManager();
        new StackerManager();
        new VanishManager();

        new PlayerTrackerListener(this);
        getServer().getScheduler().runTaskTimer(this, new PlayerTracker(), 0L, 1L);

        if (getYamlConfig().getBoolean("debug", false)) {
            AmethystLogger.debug(
                    "Debug mode is enabled, this will cause a lot of spam in the console.",
                    "Please disable debug mode in the config.yml if you do not need it."
            );
        }
    }

    @Override
    public void disable() {
        EconomyManager.disable();
        VanishManager.disable();
        CombatManager.disable();
        ProtectionManager.disable();
    }

    @Override
    public void load() {

    }

    public InventoryManager getInventoryManager(JavaPlugin plugin) {
        if (inventoryManagers.containsKey(plugin)) {
            return inventoryManagers.get(plugin);
        }

        InventoryManager inventoryManager = new InventoryManager(plugin);
        inventoryManagers.put(plugin, inventoryManager);
        inventoryManager.invoke();
        return inventoryManager;
    }

    public void setupConfigOptions() {
        AmethystLogger.setDebug(getYamlConfig().getBoolean("debug"));

        Pastebin.setAPI_KEY(getYamlConfig().getString("pastebin.api-key"));
        Pastebin.setUSER_API_KEY(getYamlConfig().getString("pastebin.user-api-key"));

        PasteProvider.getEnabledPasteTypes().clear();
        for (String key : getYamlConfig().getSection("paste-services").getRoutesAsStrings(false)) {
            boolean isEnabled = getYamlConfig().getBoolean("paste-services." + key);

            if (isEnabled) {
                PasteProvider.getEnabledPasteTypes().add(PasteProvider.PasteType.valueOf(key.toUpperCase()));
            }

        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    public JavaPlugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);
            return plugin.isEnabled() ? plugin : (JavaPlugin) Bukkit.getPluginManager().getPlugin(plugin.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return this;
        }
    }

}
