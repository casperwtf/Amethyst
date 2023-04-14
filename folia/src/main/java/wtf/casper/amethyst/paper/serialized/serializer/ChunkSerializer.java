package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Objects;

public final class ChunkSerializer implements JsonSerializer<Chunk>, JsonDeserializer<Chunk> {

    @Override
    public JsonElement serialize(final Chunk chunk, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("x", chunk.getX());
        object.addProperty("z", chunk.getZ());
        object.addProperty("world", chunk.getWorld().getName());

        return object;
    }

    @Override
    public Chunk deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = element.getAsJsonObject();

        final World world = Bukkit.getWorld(object.get("world").getAsString());

        final int x = object.get("x").getAsInt();
        final int z = object.get("z").getAsInt();

        return Objects.requireNonNull(world).getChunkAt(x, z);
    }
}