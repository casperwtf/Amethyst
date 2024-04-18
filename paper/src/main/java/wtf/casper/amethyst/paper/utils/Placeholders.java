package wtf.casper.amethyst.paper.utils;

import java.util.HashMap;
import java.util.Map;

public class Placeholders {

    public final static Placeholders EMPTY = new Placeholders();
    private final Map<String, String> placeholders = new HashMap<>();
    private final String leftDelimiter = "%";
    private final String rightDelimiter = "%";
    private String centerDelimiter = null;


    public Placeholders add(String key, String value) {
        placeholders.put(key, value);
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

            if (!(args[i + 1] instanceof String)) {
                throw new IllegalArgumentException("Value must be a component");
            }

            placeholders.put((String) args[i], (String) args[i + 1]);
        }

        return this;
    }

    public Placeholders centerify(String delimiter) {
        this.centerDelimiter = delimiter;
        return this;
    }

    public String parse(String text) {
        if (text == null) {
            return null;
        }

        if (placeholders.isEmpty()) {
            return text;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            text = text.replace(leftDelimiter + entry.getKey().replace(leftDelimiter, "").replace(rightDelimiter, "") + rightDelimiter, entry.getValue());
            if (centerDelimiter != null && text.contains(centerDelimiter)) {
                text = text.replace(centerDelimiter, "");
                text = StringUtilsPaper.centerMessageSpaces(text) + text;
            }
        }

        return text;
    }


}
