package wtf.casper.amethyst.paper.geyser;

import wtf.casper.amethyst.core.storage.impl.fstorage.JsonFStorage;
import wtf.casper.amethyst.paper.utils.GeyserUtils;

import java.io.File;
import java.util.UUID;

public class GeyserJsonStorage extends JsonFStorage<UUID, GeyserUtils.GeyserPlayer> {
    public GeyserJsonStorage(File file, Class<GeyserUtils.GeyserPlayer> type) {
        super(file, type);
    }

    @Override
    public GeyserUtils.GeyserPlayer constructValue(UUID key) {
        return new GeyserUtils.GeyserPlayer(key, null);
    }
}
