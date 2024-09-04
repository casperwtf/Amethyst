package wtf.casper.amethyst.paper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.TimeStampMode;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.DiscordWebhook;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.core.utils.pastes.PasteProvider;
import wtf.casper.amethyst.paper.hologrambridge.HologramBridge;
import wtf.casper.amethyst.paper.hooks.GeyserExpansion;
import wtf.casper.amethyst.paper.hooks.IHookController;
import wtf.casper.amethyst.paper.internal.commands.ItemSerializationCommands;
import wtf.casper.amethyst.paper.internal.listeners.LoggerListener;
import wtf.casper.amethyst.paper.internal.listeners.PlayerTracker;
import wtf.casper.amethyst.paper.internal.listeners.PlayerTrackerListener;
import wtf.casper.amethyst.paper.providers.VaultProvider;
import wtf.casper.amethyst.paper.scheduler.SchedulerUtil;
import wtf.casper.amethyst.paper.serialized.SerializableItem;
import wtf.casper.amethyst.paper.serialized.SerializableItemTypeAdapter;
import wtf.casper.amethyst.paper.serialized.serializer.*;
import wtf.casper.amethyst.paper.utils.ArmorstandUtils;
import wtf.casper.amethyst.paper.utils.ServerLock;
import wtf.casper.amethyst.paper.utils.ServerUtils;
import wtf.casper.amethyst.paper.utils.ServerVersion;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Filter;

/**
 * This class is the main class for AmethystPaper
 */
public class AmethystPaper {

    // this is to prevent relocation from changing the package name here, which would break the relocation check
    private final char[] DEFAULT_PACKAGE = new char[]{'w', 't', 'f', '.', 'c', 'a', 's', 'p', 'e', 'r', '.', 'a', 'm', 'e', 't', 'h', 'y', 's', 't', '.', 'p', 'a', 'p', 'e', 'r'};

    @Getter private static Filter filter;
    @Getter private YamlDocument amethystConfig;
    private static JavaPlugin instance;

    /**
     * This constructor is used for loading Amethyst as a plugin
     * We load dependencies here because we need to load them before the plugin is enabled
     * This is because the onLoad for plugins does not call in order of depends within plugin.yml
     *
     * @param plugin          The plugin that is shading Amethyst
     * @param relocationCheck Whether to check if the plugin is relocated
     */
    public AmethystPaper(JavaPlugin plugin, boolean relocationCheck) {
        if (relocationCheck) {
            checkRelocation();
        }

        ServerVersion.setVersion();
        instance = plugin;

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

        AmethystCore.init();
        this.amethystConfig = getYamlDocument(plugin, "amethyst-config.yml");
    }

    public void enableAmethyst(JavaPlugin plugin) {
        AmethystLogger.setLog(plugin.getLogger());
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
            PasteProvider.paste(PasteProvider.PasteType.MCLOGS,
                    "Server Version: " + Bukkit.getVersionMessage() + "\n" +
                            "Server Jar: " + ServerUtils.getServerJar() + "\n" +
                            "Java Version: " + System.getProperty("java.version") + "\n\n\n" +
                            ServerUtils.readLog(log)
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

        new PlayerTrackerListener(plugin);
        instance.getServer().getScheduler().runTaskTimer(plugin, new PlayerTracker(), 0L, 1L); // needs to be called sync cause if cancelled itll teleport

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

        PacketEvents.getAPI().getSettings().debug(false).bStats(false).checkForUpdates(true).timeStampMode(TimeStampMode.MILLIS).reEncodeByDefault(true);
        PacketEvents.getAPI().init();

        // debug nag
        if (getYamlConfig().getBoolean("debug", false)) {
            AmethystLogger.debug(
                    "Debug mode is enabled, this will cause a lot of spam in the console.",
                    "Please disable debug mode in the amethyst-config.yml if you do not need it."
            );

            new ItemSerializationCommands().register(plugin);
        }

        // initialize these
        SchedulerUtil.runLater(scheduler -> {
            ServiceUtil.getServices(IHookController.class, this.getClass().getClassLoader()).forEach(IHookController::enable);
        }, 2L);

        // Handle vault events
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            new VaultProvider(plugin);
        }

    }

    public void disableAmethyst() {
        ServiceUtil.getServices(IHookController.class, this.getClass().getClassLoader()).forEach(IHookController::disable);
        PacketEvents.getAPI().terminate();
    }

    @NotNull
    public YamlDocument getYamlConfig() {
        return amethystConfig;
    }

    public void setupConfigOptions() {
        AmethystLogger.setDebug(getYamlConfig().getBoolean("debug"));
    }

    private void checkRelocation() {
        if (AmethystPaper.class.getPackage().getName().equals(new String(DEFAULT_PACKAGE))) {
            Bukkit.getPluginManager().disablePlugin(instance);
            throw new RuntimeException("Amethyst is not relocated, please relocate it to prevent conflicts with other plugins.");
        }
    }

    public YamlDocument getYamlDocument(JavaPlugin plugin, String path) {
        return getYamlDocument(plugin, path, GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public YamlDocument getYamlDocument(JavaPlugin plugin, String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        try {
            return YamlDocument.create(
                    new File(plugin.getDataFolder(), path),
                    plugin.getResource(path),
                    generalSettings,
                    loaderSettings,
                    dumperSettings,
                    updaterSettings
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}