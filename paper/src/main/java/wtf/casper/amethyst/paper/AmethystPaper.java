package wtf.casper.amethyst.paper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.TimeStampMode;
import com.jeff_media.customblockdata.CustomBlockData;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import gg.optimalgames.hologrambridge.HologramBridge;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
import wtf.casper.amethyst.paper.listeners.PlayerBlockListener;
import wtf.casper.amethyst.paper.listeners.PlayerSmeltItemEventListener;
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
public class AmethystPaper {

    // this is to prevent relocation from changing the package name here, which would break the relocation check
    private final char[] DEFAULT_PACKAGE = new char[] {'w', 't', 'f', '.', 'c', 'a', 's', 'p', 'e', 'r', '.', 'a', 'm', 'e', 't', 'h', 'y', 's', 't', '.', 'p', 'a', 'p', 'e', 'r'};

    @Getter private static final Map<JavaPlugin, InventoryManager> inventoryManagers = new HashMap<>();
    @Getter private static Filter filter;
    @Getter @Setter private static NamespacedKey playerPlacedBlockKey;
    @Getter @Setter private static NamespacedKey playerSmeltItemKey;
    @Getter private YamlDocument amethystConfig;
    private static JavaPlugin instance;
    @Getter private CloudCommandHandler cloudCommandHandler;

    /**
     * This constructor is used for loading Amethyst as a plugin
     * We load dependencies here because we need to load them before the plugin is enabled
     * This is because the onLoad for plugins does not call in order of depends within plugin.yml
     * @param plugin The plugin that is shading Amethyst
     * @param relocationCheck Whether to check if the plugin is relocated
     */
    public AmethystPaper(JavaPlugin plugin, boolean relocationCheck) {
        if (relocationCheck) {
            checkRelocation();
        }

        instance = plugin;

        setPlayerPlacedBlockKey(new NamespacedKey(plugin, "PLAYER_PLACED_BLOCK"));
        setPlayerSmeltItemKey(new NamespacedKey(plugin, "PLAYER_SMELT_ITEM"));

        DependencyManager dependencyManager = new DependencyManager(plugin);
        dependencyManager.loadDependencies();
    }

    public AmethystPaper(JavaPlugin plugin) {
        this(plugin, true);
    }

    public static JavaPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Amethyst is not loaded. Either install amethyst plugin or shade amethyst into your plugin. Initialize with AmethystPaper(AmethystPlugin)");
        }
        return instance;
    }

    public void loadAmethyst(JavaPlugin plugin) {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();
    }

    public void initAmethyst(JavaPlugin plugin) {
        AmethystCore.init();
        this.amethystConfig = getYamlDocument(plugin, "amethyst-config.yml");

        CustomBlockData.registerListener(plugin);
        new PlayerBlockListener(plugin, this);
        new PlayerSmeltItemEventListener(plugin);;

        new ServerLock(plugin);

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

        plugin.getLogger().setFilter(filter);
        if (plugin.getDescription().getName().equals("Amethyst")) {
            new LoggerListener(instance);
        } else {
            plugin.getLogger().setFilter(AmethystPaper.getFilter());
        }

        Bukkit.getLogger().setFilter(filter);
        AmethystLogger.setFilter(filter);

        new PlayerTrackerListener(plugin);
        instance.getServer().getScheduler().runTaskTimer(plugin, new PlayerTracker(), 0L, 1L);

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
            new GeyserUtils(plugin);
        }

        new HologramBridge(plugin, true);

        EconomyManager.init();
        new CombatManager();
        new ProtectionManager();
        new StackerManager();
        new VanishManager();

        PacketEvents.getAPI().getSettings().debug(false).bStats(false).checkForUpdates(true).timeStampMode(TimeStampMode.MILLIS).reEncodeByDefault(true);
        PacketEvents.getAPI().init();

        this.cloudCommandHandler = new CloudCommandHandler();
        this.cloudCommandHandler.setup(plugin);

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
        if (Bukkit.getPluginManager().isPluginEnabled("Floodgate") && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            GeyserUtils.getGeyserStorage().write().join();
            GeyserUtils.getGeyserStorage().close().join();
        }
    }

    @NotNull
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
        if (AmethystPaper.class.getPackage().getName().equals(new String(DEFAULT_PACKAGE))) {
            Bukkit.getPluginManager().disablePlugin(instance);
            throw new RuntimeException("Amethyst is not relocated, please relocate it to prevent conflicts with other plugins.");
        }
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(JavaPlugin plugin, String path) {
        return getYamlDocument(plugin, path, GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(JavaPlugin plugin, String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        return YamlDocument.create(
                new File(plugin.getDataFolder(), path),
                plugin.getResource(path),
                generalSettings,
                loaderSettings,
                dumperSettings,
                updaterSettings
        );
    }

    public static JavaPlugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            return JavaPlugin.getProvidingPlugin(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
