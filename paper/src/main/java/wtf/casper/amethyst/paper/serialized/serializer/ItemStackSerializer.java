package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import wtf.casper.amethyst.paper.utils.Base64Utils;

import java.lang.reflect.Type;

public final class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(final ItemStack item, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("base64", Base64Utils.serializeItem(item));

        return object;
    }

    @Override
    public ItemStack deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Base64Utils.deserializeItem(element.getAsJsonObject().get("base64").getAsString());
    }
}
