package wtf.casper.amethyst.core.utils.pastes;

import java.util.concurrent.CompletableFuture;

public class PasteProvider {

    public static CompletableFuture<String> paste(PasteType type, String content) {
        return CompletableFuture.supplyAsync(() -> {
            switch (type) {
                case PASTESDEV -> {
                    CompletableFuture<String> paste = PastesDev.paste(content);
                    return paste.join();
                }
                case HASTEBIN -> {
                    CompletableFuture<String> paste = Hastebin.paste(content);
                    return paste.join();
                }
                case MCLOGS -> {
                    CompletableFuture<String> paste = MCLogs.paste(content);
                    return paste.join();
                }
            }
            throw new NullPointerException("PasteType is invalid. " + type);
        });
    }

    public enum PasteType {
        HASTEBIN,
        MCLOGS,
        PASTESDEV
    }
}
