package wtf.casper.amethyst.core.gson;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDTypeAdapter extends TypeAdapter<UUID> implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        return UUID.fromString(in.nextString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return UUID.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
