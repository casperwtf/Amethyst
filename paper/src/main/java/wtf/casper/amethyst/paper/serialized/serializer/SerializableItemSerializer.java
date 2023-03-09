package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import wtf.casper.amethyst.paper.serialized.SerializableItem;

import java.lang.reflect.Type;

public class SerializableItemSerializer implements JsonSerializer<SerializableItem>, JsonDeserializer<SerializableItem> {
    @Override
    public SerializableItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return SerializableItem.deserialize(json.getAsJsonObject());
    }

    @Override
    public JsonElement serialize(SerializableItem src, Type typeOfSrc, JsonSerializationContext context) {
        return src.serialize();
    }
}
