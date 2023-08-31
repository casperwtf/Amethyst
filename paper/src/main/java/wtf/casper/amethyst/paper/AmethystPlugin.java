package wtf.casper.amethyst.paper;

import cloud.commandframework.CommandManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.command.CloudCommandProvider;
import wtf.casper.amethyst.paper.utils.AmethystListener;

public abstract class AmethystPlugin extends JavaPlugin {

    private YamlDocument config;
    private ConfigProvider configProvider;

    // this is weird, there is no reason to have it but its more of a pain to remove it.
    @Override
    public void onLoad() {
        load();
    }

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void setName(String name) {
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

    public void enable() {
    }

    public void disable() {
    }

    public void load() {
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
        ServiceUtil.getServices(CloudCommandProvider.class, classLoader).forEach(CloudCommandProvider::registerCommands);
    }

    public void registerListeners() {
        registerListeners(getClassLoader());
    }

    public void registerListeners(ClassLoader classLoader) {
        ServiceUtil.getServices(Listener.class, classLoader).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    @SneakyThrows
    public void reloadConfig() {
        config.reload();
    }

    @Override
    @SneakyThrows
    public void saveConfig() {
        config.save();
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
