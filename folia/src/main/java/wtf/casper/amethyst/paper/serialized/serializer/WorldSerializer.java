package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;

public final class WorldSerializer implements JsonSerializer<World>, JsonDeserializer<World> {

    @Override
    public JsonElement serialize(final World world, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("name", world.getName());

        return object;
    }

    @Override
    public World deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = element.getAsJsonObject();

        return Bukkit.getWorld(object.get("name").getAsString());
    }
}
