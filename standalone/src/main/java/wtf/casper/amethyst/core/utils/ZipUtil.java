package wtf.casper.amethyst.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static void unzip(Path source, Path target) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                boolean isDirectory = zipEntry.getName().endsWith(File.separator);
                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    public static void zip(Path source, Path target) throws IOException {
        Path destination = Files.createFile(target);
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(destination))) {
            try (Stream<Path> walk = Files.walk(source)) {
                walk.filter(path -> !Files.isDirectory(path))
                        .filter(path -> !path.toString().endsWith(".tmp"))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(source.relativize(path).toString());
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }

    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
        return targetDir.resolve(zipEntry.getName()).normalize();
    }

}
