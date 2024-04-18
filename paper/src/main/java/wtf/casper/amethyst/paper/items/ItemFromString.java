package wtf.casper.amethyst.paper.items;

import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.core.utils.ServiceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ItemFromString {
    private final static List<ItemSerializer> serializers = new ArrayList<>();

    static {
        ServiceUtil.getServices(ItemSerializer.class, ItemFromString.class.getClassLoader()).forEach(ItemFromString::register);
    }

    public static void register(ItemSerializer serializer) {
        if (serializers.contains(serializer)) {
            return;
        }
        serializers.add(serializer);
    }

    public static String serialize(ItemStack itemStack) {

        StringBuilder builder = new StringBuilder();

        for (int i = serializers.size() - 1; i >= 0; i--) {
            ItemSerializer serializer = serializers.get(i);
            String serialized = serializer.deserializeType(itemStack).orElse(null);
            if (serialized != null) {
                builder.append(serialized);
                break;
            }
        }

        if (builder.isEmpty()) {
            throw new IllegalStateException("No serializer found for item: " + itemStack);
        }

        for (int i = serializers.size() - 1; i >= 0; i--) {
            ItemSerializer serializer = serializers.get(i);
            String serialized = serializer.deserializeMeta(itemStack).orElse(null);
            if (serialized != null) {
                builder.append(" ");
                builder.append(serialized);
                break;
            }
        }

        return builder.toString().trim();
    }

    public static ItemStack deserialize(String itemString) {
        Iterator<String> iter = Arrays.stream(itemString.split(" ")).iterator();

        if (!iter.hasNext()) {
            throw new IllegalArgumentException("Invalid item string: " + itemString);
        }

        StringBuilder builder = new StringBuilder();

        String next;
        do {
            next = iter.next();
            builder.append(next);
            builder.append(" ");
        } while (needsContinuation(builder.toString()) && iter.hasNext());

        ItemStack baseStack = null;

        for (ItemSerializer serializer : serializers) {
            baseStack = serializer.initialStack(builder.toString().trim()).orElse(null);
            if (baseStack != null) {
                break;
            }
        }

        if (baseStack == null) {
            throw new IllegalArgumentException("Invalid item string: " + itemString);
        }

        while (iter.hasNext()) {
            StringBuilder nextBuilder = new StringBuilder();
            String nextPart;
            do {
                nextPart = iter.next();
                nextBuilder.append(nextPart);
                nextBuilder.append(" ");
            } while (needsContinuation(nextBuilder.toString()) && iter.hasNext());

            nextPart = nextBuilder.toString().trim();

            for (ItemSerializer serializer : serializers) {
                if (serializer.tryAppend(baseStack, nextPart)) {
                    break;
                }
            }
        }

        return baseStack;
    }

    private static boolean needsContinuation(String next) {
        if (!next.contains("\"")) {
            return false;
        }

        char[] chars = next.toCharArray();
        boolean isEscaped = false;
        boolean isSingleQuoted = false;
        boolean isDoubleQuoted = false;
        for (char aChar : chars) {
            if (aChar == '\\') {
                isEscaped = !isEscaped;
            } else if (aChar == '"' && !isEscaped) {
                if (!isSingleQuoted) {
                    isSingleQuoted = true;
                    continue;
                }

                isDoubleQuoted = true;
                break;
            }
        }

        return !isDoubleQuoted;
    }
}
