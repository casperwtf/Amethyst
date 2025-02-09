package wtf.casper.amethyst.paper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.inject.InjectionContainer;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.command.CloudCommand;
import wtf.casper.amethyst.paper.providers.CloudCommandProvider;
import wtf.casper.amethyst.paper.providers.ConfigProvider;

import java.io.IOException;
import java.util.List;

public abstract class AmethystPlugin extends JavaPlugin {

    private YamlDocument config;
    private ConfigProvider configProvider;
    private CloudCommandProvider cloudCommandHandler;
    @Getter private InjectionContainer injectionContainer = new InjectionContainer();

    public void setName(String name) {
        try {
            PluginMeta meta = getPluginMeta();

            if (meta.getName().equals(name)) return;

            if (getServer().getPluginManager().getPlugin(name) != null) {
                AmethystLogger.log("Plugin with name " + name + " already exists!");
                return;
            }

            if (!name.matches("[a-zA-Z0-9_\\-.]+")) {
                AmethystLogger.log("Invalid name for plugin! Name must be a-z,A-Z,0-9,_,.,-");
                return;
            }

            ReflectionUtil.setPrivateField(meta, "name", name);
            Class.forName("", false, getClassLoader());

            return;
        } catch (Exception ignored) {
        }

        PluginDescriptionFile descriptionFile = getDescription();
        if (descriptionFile.getName().equals(name)) return;

        if (getServer().getPluginManager().getPlugin(name) != null) {
            AmethystLogger.log("Plugin with name " + name + " already exists!");
            return;
        }
        if (!name.matches("[a-zA-Z0-9_\\-.]+")) {
            AmethystLogger.log("Invalid name for plugin! Name must be a-z,A-Z,0-9,_,.,-");
            return;
        }
        ReflectionUtil.setPrivateField(descriptionFile, "name", name);
    }

    @NotNull
    @Override
    @Deprecated
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    @NotNull
    public YamlDocument getYamlConfig() {
        return config;
    }

    public void registerCommands() {
        registerCommands(getClassLoader());
    }

    public void registerCommands(ClassLoader classLoader) {
        List<CloudCommand> services = ServiceUtil.getServices(CloudCommand.class, classLoader);
        services.forEach(cloudCommand -> cloudCommand.registerCommands(this));
        getLogger().info("Registered " + services.size() + " commands");
        for (CloudCommand service : services) {
            getLogger().info("- " + service.getClass().getSimpleName());
        }
    }

    public void registerListeners() {
        registerListeners(getClassLoader());
    }

    public void registerListeners(ClassLoader classLoader) {
        List<Listener> services = ServiceUtil.getServices(Listener.class, classLoader);
        services.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        getLogger().info("Registered " + services.size() + " listeners");
        for (Listener service : services) {
            getLogger().info("- " + service.getClass().getSimpleName());
        }
    }

    public void setupCommands() {
        cloudCommandHandler = new CloudCommandProvider();
        this.cloudCommandHandler.setup(this);
    }

    public void deconstructCommands() {
        this.cloudCommandHandler.setCommandManager(null);
        this.cloudCommandHandler = null;
    }

    @Override
    public void reloadConfig() {
        try {
            config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        loadConfig();
    }

    public void loadConfig() {
        config = getYamlDocument("config.yml");
    }

    public YamlDocument getYamlDocument(String name) {
        if (configProvider == null) configProvider = new ConfigProvider(this);
        return configProvider.getYamlDocument(name);
    }

    public YamlDocument getYamlDocumentVersioned(String name) {
        if (configProvider == null) configProvider = new ConfigProvider(this);
        return configProvider.getYamlDocumentVersioned(name);
    }

    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        if (configProvider == null) configProvider = new ConfigProvider(this);
        return configProvider.getYamlDocument(path, generalSettings, loaderSettings, dumperSettings, updaterSettings);
    }
}
