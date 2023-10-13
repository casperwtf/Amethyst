package wtf.casper.amethyst.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.velocity.listener.PlayerListener;
import wtf.casper.amethyst.velocity.redis.RedisManager;
import wtf.casper.storageapi.libs.boostedyaml.YamlDocument;
import wtf.casper.storageapi.libs.boostedyaml.settings.dumper.DumperSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.general.GeneralSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.loader.LoaderSettings;
import wtf.casper.storageapi.libs.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "velocity",
        name = "AmethystVelocity",
        version = "1.0.0"
)
@Getter
public class AmethystVelocity {

    private final Path dataDirectory;
    private Logger logger;
    private ProxyServer proxy;
    private YamlDocument config;
    private RedisManager redisManager;

    @Inject
    public AmethystVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        AmethystCore.init();

        new PlayerListener(this);
        this.redisManager = new RedisManager(this);
        this.config = getYamlDocument("config.yml");

        AmethystLogger.log("AmethystVelocity is enabled!");
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path) {
        return getYamlDocument(path, GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    @SneakyThrows
    public YamlDocument getYamlDocument(String path, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        return YamlDocument.create(new File(dataDirectory.toFile(), path),
                getClass().getResourceAsStream(path),
                generalSettings,
                loaderSettings,
                dumperSettings,
                updaterSettings);
    }
}
