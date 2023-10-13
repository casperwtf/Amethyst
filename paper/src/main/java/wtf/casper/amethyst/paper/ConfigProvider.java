package wtf.casper.amethyst.paper;

import wtf.casper.storageapi.libs.boostedyaml.YamlDocument;
import wtf.casper.storageapi.libs.boostedyaml.dvs.versioning.BasicVersioning;
import wtf.casper.storageapi.libs.boostedyaml.settings.dumper.DumperSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.general.GeneralSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.loader.LoaderSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.updater.UpdaterSettings;
import lombok.SneakyThrows;
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
