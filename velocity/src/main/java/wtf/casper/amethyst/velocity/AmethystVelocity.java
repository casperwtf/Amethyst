package wtf.casper.amethyst.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.velocity.listener.PlayerListener;
import wtf.casper.amethyst.velocity.redis.RedisManager;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "velocity",
        name = "AmethystVelocity",
        version = "1.0.0"
)
@Getter
public class AmethystVelocity {

    private Logger logger;
    private ProxyServer proxy;
    private final Path dataDirectory;
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
