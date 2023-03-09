package wtf.casper.amethyst.core.storage;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Credentials {

    private final StorageType type;

    @Nullable private String host;
    @Nullable private String username;
    @Nullable private String password;
    @Nullable private String database;
    @Nullable private String collection;
    @Nullable private String uri;
    private int port;

    public static Credentials of(final StorageType type, @Nullable final String host, @Nullable final String username, @Nullable final String password, @Nullable final String database, @Nullable String collection, @Nullable final String uri, final int port) {
        return new Credentials(type, host, username, password, database, collection, uri, port);
    }

    public static Credentials from(final YamlDocument config, String path) {
        final Section section = config.getSection(path);

        return of(StorageType.valueOf(section.getString("type", "JSON").toUpperCase()),
                section.getString("host", null),
                section.getString("username", null),
                section.getString("password", null),
                section.getString("database", null),
                section.getString("collection", null),
                section.getString("uri", null),
                section.getOptionalString("port").map(Integer::parseInt).orElse(section.getInt("port", 3306))
        );
    }

}
