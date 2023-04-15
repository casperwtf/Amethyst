package wtf.casper.amethyst.paper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.ReflectionUtil;

import java.io.File;
import java.util.Optional;

public abstract class AmethystPlugin extends JavaPlugin {

    private YamlDocument config;
    @Getter @Setter private NamespacedKey playerPlacedBlockKey;

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

    @SneakyThrows
    public void loadConfig() {
        config = getYamlDocument("config.yml");
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path) {
        return getYamlDocument(path, GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        return YamlDocument.create(new File(getDataFolder(), path),
                getResource(path),
                generalSettings,
                loaderSettings,
                dumperSettings,
                updaterSettings);
    }

    public AmethystPlugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(clazz);
            if (plugin instanceof AmethystPlugin) {
                return (AmethystPlugin) plugin;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
