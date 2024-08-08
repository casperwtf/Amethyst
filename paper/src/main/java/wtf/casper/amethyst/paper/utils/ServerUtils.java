package wtf.casper.amethyst.paper.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ServerUtils {

    @Getter private static final String serverJar = Bukkit.getVersionMessage();

    private ServerUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    /**
     * Get the plugin that called this method. This is caller sensitive & cannot be cached.
     * @return The plugin that called this method.
     */
    public static JavaPlugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            return JavaPlugin.getProvidingPlugin(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read the contents of a log file.
     * @param log The log file to read.
     * @return The contents of the log file, or null if an error occurred.
     */
    @Nullable
    public static String readLog(File log) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(log.toPath());
        } catch (IOException e) {
            return null;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
