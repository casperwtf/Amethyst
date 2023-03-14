package wtf.casper.amethyst.paper;

import com.google.common.collect.Lists;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.logging.Level;

public class DependencyManager {
    private final AmethystPlugin plugin;
    private final BukkitLibraryManager libraryManager;

    public DependencyManager(AmethystPlugin plugin) {
        this.plugin = plugin;
        this.libraryManager = new BukkitLibraryManager(plugin);
        this.libraryManager.addMavenCentral();
        this.libraryManager.addJitPack();
        this.libraryManager.addSonatype();
        this.libraryManager.addRepository("https://repo.codemc.io/repository/maven-snapshots/");
        this.libraryManager.addRepository("https://repo.opencollab.dev/maven-releases/");
        this.libraryManager.addRepository("https://repo.minebench.de/");
        this.libraryManager.addRepository("https://redempt.dev");
        this.libraryManager.addRepository("https://hub.jeff-media.com/nexus/repository/jeff-media-public/");
    }

    private Library getLibrary(String groupId, String artifactId, String version, String pattern, String relocatePattern) {
        Library.Builder builder = Library.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version);

        if (!relocatePattern.isEmpty()) {
            builder.relocate(pattern, relocatePattern);
        }

        return builder.build();
    }

    public void loadDependencies() {
        this.plugin.getLogger().log(Level.INFO, "Loading dependencies...");

        List<Library> libraries = Lists.newArrayList(
                this.getLibrary("dev{}dejvokep", "boosted-yaml", "1.3", "dev{}dejvokep{}boostedyaml", "wtf{}casper{}amethyst{}libs{}boostedyaml"),
                this.getLibrary("io{}github{}rysefoxx{}inventory", "RyseInventory-Plugin", "1.5.7", "", ""),
                this.getLibrary("de{}themoep", "minedown-adventure", "1.7.1-SNAPSHOT", "de{}themoep{}minedown", "wtf{}casper{}amethyst{}libs{}minedown"),
                this.getLibrary("com{}github{}chubbyduck1", "HologramBridge", "1.1.0", "", ""),
                this.getLibrary("com{}google{}code{}gson", "gson", "2.10.1", "com{}google{}gson", "wtf{}casper{}amethyst{}libs{}google{}gson"),
                // this does not want to load via libby, not sure why
//                this.getLibrary("me{}gamercoder215", "mobchip-bukkit", "1.8.1-SNAPSHOT", "me{}gamercoder215{}mobchip", "wtf{}casper{}amethyst{}libs{}mobchip"),
                this.getLibrary("org{}mongodb", "mongo-java-driver", "3.12.11", "", ""),
                this.getLibrary("io{}lettuce", "lettuce-core", "6.2.3.RELEASE", "io{}lettuce{}core", "wtf{}casper{}amethyst{}libs{}lettuce"),
                this.getLibrary("io{}projectreactor", "reactor-core", "3.5.3", "", ""),
                this.getLibrary("org{}reactivestreams", "reactive-streams", "1.0.4", "", ""),
                this.getLibrary("com{}github{}Redempt", "Crunch", "1.0", "", ""),
                this.getLibrary("org{}quartz-scheduler", "quartz", "2.3.2", "", ""),
                this.getLibrary("com{}github{}ben-manes{}caffeine", "caffeine", "3.1.5", "", ""),
                this.getLibrary("org{}reflections", "reflections", "0.10.2", "", ""),
                this.getLibrary("com{}zaxxer", "HikariCP", "5.0.1", "com{}zaxxer.hikari", "wtf{}casper{}amethyst{}libs{}hikari"),
                this.getLibrary("com{}jeff_media", "CustomBlockData", "2.2.0", "com{}jeff_media{}customblockdata", "wtf{}casper{}amethyst{}libs{}customblockdata")
        );
        for (Library library : libraries) {
            this.libraryManager.loadLibrary(library);
        }

        this.plugin.getLogger().log(Level.INFO, "Successfully loaded dependencies.");
    }
}
