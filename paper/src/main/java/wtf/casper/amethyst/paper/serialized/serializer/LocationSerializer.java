package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public final class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(final Location location, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        object.addProperty("yaw", location.getYaw());
        object.addProperty("pitch", location.getPitch());
        object.addProperty("world", location.getWorld() == null ? "" : location.getWorld().getName());

        return object;
    }

    @Override
    public Location deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject object = element.getAsJsonObject();

        final World world = Bukkit.getWorld(object.get("world").getAsString());
        final double x = object.get("x").getAsDouble();
        final double y = object.get("y").getAsDouble();
        final double z = object.get("z").getAsDouble();
        final float yaw = object.get("yaw").getAsFloat();
        final float pitch = object.get("pitch").getAsFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }
}
