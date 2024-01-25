package wtf.casper.amethyst.paper.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ServerUtils {

    private static String serverJar = null;

    private ServerUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }

    @SneakyThrows
    public static String getServerJar(final File log) {
        if (serverJar != null) {
            return serverJar;
        }

        final BufferedReader reader = new BufferedReader(new FileReader(log));

        final String[] jar = new String[1];
        jar[0] = "Unknown";
        reader.lines().parallel().forEach(string -> {

            if (!string.contains("This server is running")) {
                return;
            }

            jar[0] = string;
        });

        serverJar = jar[0];
        return serverJar;
    }

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
