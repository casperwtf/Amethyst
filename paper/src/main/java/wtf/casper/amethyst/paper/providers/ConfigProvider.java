package wtf.casper.amethyst.paper.providers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
            YamlDocument document = getYamlDocument(path,
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
            );

            try {
                if (!document.getDefaults().getString("config-version").equals(document.getString("config-version"))) {
                    document.update();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        try {
            return YamlDocument.create(new File(plugin.getDataFolder(), path),
                    plugin.getResource(path),
                    generalSettings,
                    loaderSettings,
                    dumperSettings,
                    updaterSettings);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load yaml document", e);
        }
    }

}
