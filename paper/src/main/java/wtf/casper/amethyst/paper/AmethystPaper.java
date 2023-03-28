package wtf.casper.amethyst.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.dejvokep.boostedyaml.YamlDocument;
import gg.optimalgames.hologrambridge.HologramBridge;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
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
import wtf.casper.amethyst.paper.listeners.LoggerListener;
import wtf.casper.amethyst.paper.serialized.SerializableItem;
import wtf.casper.amethyst.paper.serialized.SerializableItemTypeAdapter;
import wtf.casper.amethyst.paper.serialized.serializer.*;
import wtf.casper.amethyst.paper.tracker.PlayerTracker;
import wtf.casper.amethyst.paper.tracker.PlayerTrackerListener;
import wtf.casper.amethyst.paper.utils.ArmorstandUtils;
import wtf.casper.amethyst.paper.utils.GeneralUtils;
import wtf.casper.amethyst.paper.utils.GeyserUtils;
import wtf.casper.amethyst.paper.utils.ServerLock;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Filter;

/**
 * This class is the main class for AmethystPaper
 */
public class AmethystPaper extends AmethystPlugin {

    // this is to prevent relocation from changing the package name here, which would break the relocation check
    private final char[] DEFAULT_PACKAGE = new char[] {'w', 't', 'f', '.', 'c', 'a', 's', 'p', 'e', 'r', '.', 'a', 'm', 'e', 't', 'h', 'y', 's', 't', '.', 'p', 'a', 'p', 'e', 'r'};

    @Getter private static final Map<JavaPlugin, InventoryManager> inventoryManagers = new HashMap<>();
    @Getter private static Filter filter;
    @Getter private YamlDocument amethystConfig;
    private final boolean isLoadedFromPlugin;
    @Getter private static AmethystPlugin instance;
    private GeyserUtils geyserUtils;
    /**
     * This constructor is used for loading Amethyst as a plugin
     * We load dependencies here because we need to load them before the plugin is enabled
     * This is because the onLoad for plugins does not call in order of depends in plugin.yml
     */
    public AmethystPaper() {
        super();

        this.isLoadedFromPlugin = true;

        DependencyManager dependencyManager = new DependencyManager(this);
        dependencyManager.loadDependencies();
    }

    /**
     * This constructor is used for shading purposes
     * @param plugin The plugin that is shading Amethyst
     */
    public AmethystPaper(AmethystPlugin plugin) {
        this.isLoadedFromPlugin = false;
        instance = plugin;

        setPlayerPlacedBlockKey(new NamespacedKey(plugin, "PLAYER_PLACED_BLOCK"));

        DependencyManager dependencyManager = new DependencyManager(plugin);
        dependencyManager.loadDependencies();

        checkRelocation();

        initAmethyst(plugin);
    }

    @Override
    public void disable() {
        disableAmethyst();
    }

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        setPlayerPlacedBlockKey(new NamespacedKey(this, "PLAYER_PLACED_BLOCK"));
        initAmethyst(this);
    }

    public static AmethystPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Amethyst is not loaded. Either install amethyst plugin or shade amethyst into your plugin. Initialize with AmethystPaper(AmethystPlugin)");
        }
        return instance;
    }

    public void initAmethyst(AmethystPlugin plugin) {
        AmethystCore.init();
        this.amethystConfig = getYamlDocument("amethyst-config.yml");

        CustomBlockData.registerListener(plugin);

        new ServerLock(plugin);
        geyserUtils = new GeyserUtils(plugin);

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

        if (isLoadedFromPlugin) {
            getLogger().setFilter(filter);
        } else {
            plugin.getLogger().setFilter(filter);
        }
        Bukkit.getLogger().setFilter(filter);
        AmethystLogger.getLogger().setFilter(filter);

        if (isLoadedFromPlugin) {
            new LoggerListener(this);
        }

        new PlayerTrackerListener(plugin);
        getServer().getScheduler().runTaskTimer(plugin, new PlayerTracker(), 0L, 1L);

        new ArmorstandUtils(plugin);

        setupConfigOptions();
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

        new HologramBridge(plugin, true);

        EconomyManager.init();
        new CombatManager();
        new ProtectionManager();
        new StackerManager();
        new VanishManager();

        if (getYamlConfig().getBoolean("debug", false)) {
            AmethystLogger.debug(
                    "Debug mode is enabled, this will cause a lot of spam in the console.",
                    "Please disable debug mode in the amethyst-config.yml if you do not need it."
            );
        }
    }

    public void disableAmethyst() {
        EconomyManager.disable();
        VanishManager.disable();
        CombatManager.disable();
        ProtectionManager.disable();
        geyserUtils.getGeyserStorage().write().join();
        geyserUtils.getGeyserStorage().close().join();
    }

    @NotNull
    @Override
    public YamlDocument getYamlConfig() {
        return amethystConfig;
    }

    public static InventoryManager getInventoryManager(JavaPlugin plugin) {
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

    private void checkRelocation() {
        if (isLoadedFromPlugin) {
            return;
        }
        if (AmethystPaper.class.getPackage().getName().equals(new String(DEFAULT_PACKAGE))) {
            AmethystLogger.error("Amethyst is not relocated, please relocate it to prevent conflicts with other plugins.");
            Bukkit.getPluginManager().disablePlugin(instance);
        }
    }
}
