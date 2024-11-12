package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.paper.utils.Base64Utils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public final class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(final ItemStack item, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("item", new String(item.serializeAsBytes(), StandardCharsets.UTF_8));

        return object;
    }

    @Override
    public ItemStack deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = element.getAsJsonObject();

        return ItemStack.deserializeBytes(object.get("item").getAsString().getBytes());
    }
}
