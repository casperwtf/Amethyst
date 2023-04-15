package wtf.casper.amethyst.paper.serialized;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SerializableItemTypeAdapter implements JsonSerializer<SerializableItem>, JsonDeserializer<SerializableItem> {

    @Override
    public JsonElement serialize(SerializableItem serializableItem, Type type, JsonSerializationContext jsonSerializationContext) {
        return serializableItem.serialize();
    }

    @Override
    public SerializableItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            return SerializableItem.deserialize(jsonElement.getAsJsonObject());
        } else {
            throw new JsonParseException("Improperly formatted json element");
        }
    }

}
