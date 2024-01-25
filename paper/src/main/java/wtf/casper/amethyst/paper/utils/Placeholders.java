package wtf.casper.amethyst.paper.utils;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Placeholders {

    public final static Placeholders EMPTY = new Placeholders();

    private final static Map<String, Pattern> regexCache = new HashMap<>();

    private final Map<String, Component> placeholders = new HashMap<>();

    public Placeholders add(String key, Component value) {
        placeholders.put("%" + key + "%", value);
        return this;
    }

    public Placeholders add(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be in pairs");
        }

        for (int i = 0; i < args.length; i += 2) {
            if (!(args[i] instanceof String)) {
                throw new IllegalArgumentException("Key must be a string");
            }

            if (!(args[i + 1] instanceof Component)) {
                throw new IllegalArgumentException("Value must be a component");
            }

            add((String) args[i], (Component) args[i + 1]);
        }

        return this;
    }


    public Component parse(Component component) {
        if (component == null) {
            return null;
        }

        if (placeholders.isEmpty()) {
            return component;
        }

        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            component = component.replaceText(builder -> builder.match(regexOfKey(entry.getKey())).replacement(entry.getValue()));
        }
        return component;
    }

    public Component replace(Component component) {
        return parse(component);
    }

    private Pattern regexOfKey(String key) {
        if (regexCache.containsKey(key)) {
            return regexCache.get(key);
        }

        Pattern pattern = Pattern.compile(key);
        regexCache.put(key, pattern);
        return pattern;
    }
}
