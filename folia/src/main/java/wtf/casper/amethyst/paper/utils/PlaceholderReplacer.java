package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;
import wtf.casper.amethyst.core.utils.AmethystLogger;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderReplacer {

    private final Map<String, String> placeholders;
    private String center = null;

    public PlaceholderReplacer() {
        placeholders = new HashMap<>();
    }

    public String[] getPlaceholders() {
        String[] placeholders = new String[this.placeholders.size() * 2];

        int i = 0;
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            placeholders[i] = entry.getKey();
            placeholders[i + 1] = entry.getValue();
            i += 2;
        }

        return placeholders;
    }

    public PlaceholderReplacer center(String key) {
        center = "{" + key + "}";
        return this;
    }

    public PlaceholderReplacer add(String key, String value) {
        placeholders.put("{" + key + "}", value);
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
        if (center != null && args.contains(center)) {
            args = StringUtilsPaper.centerMessage(args.replace(center, ""));
        }
        return args;
    }

    public Component parse(Component component) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                AmethystLogger.debug("PlaceholderReplacer", "Key or value is null, skipping");
                continue;
            }
            component = component.replaceText(builder -> {
                builder.matchLiteral(entry.getKey()).replacement(Component.text(entry.getValue()));
            });
        }
        return component;
    }

    public String replace(String args) {
        return parse(args);
    }
}
