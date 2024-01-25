package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MiniMessageCache {

    private final MiniMessage miniMessage;
    private final Map<String, Component> cache = new HashMap<>();

    public MiniMessageCache() {
        this.miniMessage = MiniMessage.builder().build();
    }

    public MiniMessageCache(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public Component get(@NotNull String text) {
        if (cache.containsKey(text)) {
            return cache.get(text);
        }

        Component component = miniMessage.deserialize(text);
        cache.put(text, component);
        return component;
    }
}
