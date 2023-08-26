package wtf.casper.amethyst.paper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigProvider {

    private final JavaPlugin plugin;

    public ConfigProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public YamlDocument getYamlDocument(String path) {
        try {
            return getYamlDocument(path,
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT,
                    DumperSettings.DEFAULT,
                    UpdaterSettings.DEFAULT
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public YamlDocument getYamlDocumentVersioned(String path) {
        try {
            return getYamlDocument(path,
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @SneakyThrows
    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        return YamlDocument.create(new File(plugin.getDataFolder(), path),
                plugin.getResource(path),
                generalSettings,
                loaderSettings,
                dumperSettings,
                updaterSettings);
    }

}
