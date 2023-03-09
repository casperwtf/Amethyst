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
                switch (enabledPasteType) {
                    case PASTESDEV -> {
                        CompletableFuture<String> paste = PastesDev.paste(content);
                        String join = paste.join();
                        if (join == null) {
                            continue;
                        }
                        return join;
                    }
                    case HASTEBIN -> {
                        CompletableFuture<String> paste = Hastebin.paste(content);
                        String join = paste.join();
                        if (join == null) {
                            continue;
                        }
                        return join;
                    }
                    case PASTEBIN -> {
                        CompletableFuture<String> paste = Pastebin.paste(content);
                        String join = paste.join();
                        if (join == null) {
                            continue;
                        }
                        return join;
                    }
                    case MCLOGS -> {
                        CompletableFuture<String> paste = MCLogs.paste(content);
                        String join = paste.join();
                        if (join == null) {
                            continue;
                        }
                        return join;
                    }
                }
            }

            return null;
        });
    }

    public enum PasteType {
        PASTEBIN,
        HASTEBIN,
        MCLOGS,
        PASTESDEV
    }
}
