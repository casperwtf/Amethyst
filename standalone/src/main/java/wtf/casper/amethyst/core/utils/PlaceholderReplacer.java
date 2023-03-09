package wtf.casper.amethyst.core.utils;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderReplacer {

    private final Map<String, String> placeholders;
    private String center = null;

    public PlaceholderReplacer() {
        placeholders = new HashMap<>();
    }

    public PlaceholderReplacer center(String key) {
        center = key;
        return this;
    }

    public PlaceholderReplacer add(String key, String value) {
        placeholders.put(key, value);
        return this;
    }

    public String parse(String args) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                AmethystLogger.debug("PlaceholderReplacer", "Key or value is null, skipping");
                continue;
            }
            args = args.replace(entry.getKey(), entry.getValue());
        }
        return args;
    }
}
