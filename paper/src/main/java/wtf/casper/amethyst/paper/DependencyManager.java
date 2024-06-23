package wtf.casper.amethyst.paper;

import com.google.common.collect.Lists;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.relocation.Relocation;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class DependencyManager {
    private final JavaPlugin plugin;
    private final BukkitLibraryManager libraryManager;

    public DependencyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.libraryManager = new BukkitLibraryManager(plugin);
        this.libraryManager.addMavenCentral();
        this.libraryManager.addJitPack();
        this.libraryManager.addSonatype();
        this.libraryManager.addRepository("https://repo.codemc.io/repository/maven-snapshots/");
        this.libraryManager.addRepository("https://repo.codemc.io/repository/maven-releases/");
        this.libraryManager.addRepository("https://repo.opencollab.dev/maven-releases/");
        this.libraryManager.addRepository("https://repo.minebench.de/");
        this.libraryManager.addRepository("https://redempt.dev");
        this.libraryManager.addRepository("https://hub.jeff-media.com/nexus/repository/jeff-media-public/");
        this.libraryManager.addRepository("https://nexus.iridiumdevelopment.net/repository/maven-releases/");
        this.libraryManager.addRepository("https://oss.sonatype.org/content/repositories/snapshots");
        this.libraryManager.addRepository("https://s01.oss.sonatype.org/content/repositories/snapshots/");
    }

    private Library getLibrary(String groupId, String artifactId, String version, String... patterns) {
        Library.Builder builder = Library.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version);

        if (patterns != null && patterns.length % 2 == 0 && patterns.length > 0) {
            for (int i = 0; i < patterns.length; i += 2) {
                builder.relocate(new Relocation(patterns[i], patterns[i + 1]));
            }
        }

        return builder.build();
    }

    public void loadDependencies() {
        this.plugin.getLogger().log(Level.INFO, "Loading dependencies...");
        String amethystPackage = "wtf.casper.amethyst"; // done as string so relocation changes this to the correct package
        amethystPackage = amethystPackage.replace(".", "{}"); // need the {} because relocation would relocate the string literal

        // gets split up because relocation is weird, this gets converted to "wtf.casper.amethyst.libs.reactor" if this doesn't happen
        String reactor = "re";
        String javaassist = "jav";

        List<Library> libraries = Lists.newArrayList(
                this.getLibrary("org{}mariadb{}jdbc", "mariadb-java-client", "3.1.3",
                        "org{}mariadb{}jdbc", amethystPackage + "{}libs{}mariadb"
                ),
                this.getLibrary("com{}zaxxer", "HikariCP", "5.0.1",
                        "com{}zaxxer{}hikari", amethystPackage + "{}libs{}hikari"
                ),
                this.getLibrary("com{}jeff_media", "CustomBlockData", "2.2.0",
                        "com{}jeff_media{}customblockdata", amethystPackage + "{}libs{}customblockdata"
                ),
                this.getLibrary("io{}lettuce", "lettuce-core", "6.2.3.RELEASE",
                        "org{}reactivestreams", amethystPackage + "{}libs{}reactivestreams",
                        reactor + "actor", amethystPackage + "{}libs{}reactor",
                        "io{}lettuce{}core", amethystPackage + "{}libs{}lettuce"
                ),
                this.getLibrary("io{}projectreactor", reactor + "actor" + "-core", "3.5.3",
                        reactor + "actor", amethystPackage + "{}libs{}reactor",
                        "org{}reactivestreams", amethystPackage + "{}libs{}reactivestreams"
                ),
                this.getLibrary("dev{}dejvokep", "boosted-yaml", "1.3",
                        "dev{}dejvokep{}boostedyaml", amethystPackage + "{}libs{}boostedyaml"
                ),
                this.getLibrary("com{}github{}chubbyduck1", "HologramBridge", "1.1.0",
                        "gg{}optimalgames{}hologrambridge", amethystPackage + "{}libs{}hologrambridge"
                ),
                this.getLibrary("com{}google{}code{}gson", "gson", "2.10.1",
                        "com{}google", amethystPackage + "{}libs{}google"
                ),
                this.getLibrary("com{}google{}guava", "guava", "33.2.1-jre",
                        "com{}google", amethystPackage + "{}libs{}google"
                ),
                this.getLibrary("org{}mongodb", "mongo-java-driver", "3.12.11",
                        "com{}mongodb", amethystPackage + "{}libs{}mongodb"
                ),
                this.getLibrary("org{}reactivestreams", "reactive-streams", "1.0.4",
                        "org{}reactivestreams", amethystPackage + "{}libs{}reactivestreams"
                ),
                this.getLibrary("com{}github{}Redempt", "Crunch", "1.0",
                        "redempt{}crunch", amethystPackage + "{}libs{}crunch"
                ),
                this.getLibrary("org{}quartz-scheduler", "quartz", "2.3.2",
                        "org{}quartz", amethystPackage + "{}libs{}quartz"
                ),
                this.getLibrary("com{}github{}ben-manes{}caffeine", "caffeine", "3.1.5",
                        "com{}github{}benmanes{}caffeine", amethystPackage + "{}libs{}caffeine"
                ),
                this.getLibrary("org{}reflections", "reflections", "0.10.2",
                        "org{}reflections", amethystPackage + "{}libs{}reflections",
                        javaassist + "assist", amethystPackage + "{}libs{}javassist"
                ),
                // so much cloud shit wtf
                this.getLibrary("io{}leangen{}geantyref", "geantyref", "1.3.15",
                        "io{}leangen{}geantyref", amethystPackage + "{}libs{}geantyref"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-core", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-services", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-tasks", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-brigadier", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-bukkit", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-paper", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-minecraft-extras", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("cloud{}commandframework", "cloud-annotations", "1.8.3",
                        "cloud{}commandframework", amethystPackage + "{}libs{}cloud{}commandframework"
                ),
                this.getLibrary("com{}github{}retrooper{}packetevents", "spigot", "2.2.1",
                        "com{}github{}retrooper{}packetevents", amethystPackage + "{}libs{}packetevents{}api",
                        "io{}github{}retrooper{}packetevents", amethystPackage + "{}libs{}packetevents{}impl",
                        "net{}kyori", amethystPackage + "{}libs{}kyori"
                ),
                this.getLibrary("net{}kyori", "adventure-api", "4.14.0",
                        "net{}kyori", amethystPackage + "{}libs{}kyori"
                ),
                this.getLibrary("net{}kyori", "adventure-platform-bukkit", "4.3.0",
                        "net{}kyori", amethystPackage + "{}libs{}kyori"
                )
        );

        for (Library library : libraries) {
            try {
                this.libraryManager.loadLibrary(library);
            } catch (RuntimeException exception) {
                System.out.println("Failed to load library " + library.getArtifactId() + " " + library.getVersion());
                exception.printStackTrace();
            }
        }

        this.plugin.getLogger().log(Level.INFO, "Successfully loaded dependencies.");
    }
}
