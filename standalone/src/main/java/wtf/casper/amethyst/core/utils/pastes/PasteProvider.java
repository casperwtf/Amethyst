package wtf.casper.amethyst.core.utils.pastes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PasteProvider {

    @Getter
    private static final List<PasteType> enabledPasteTypes = new ArrayList<>();

    public static CompletableFuture<String> paste(String content) {
        return CompletableFuture.supplyAsync(() -> {

            for (PasteType enabledPasteType : enabledPasteTypes) {
                CompletableFuture<String> paste = paste(content, enabledPasteType);
                String join = paste.join();
                if (join != null) {
                    return join;
                }
            }

            return null;
        });
    }

    public static CompletableFuture<String> paste(String content, PasteType type) {
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
