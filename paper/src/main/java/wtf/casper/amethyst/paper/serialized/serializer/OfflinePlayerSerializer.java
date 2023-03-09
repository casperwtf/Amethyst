package wtf.casper.amethyst.paper.serialized.serializer;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.util.UUID;

public final class OfflinePlayerSerializer implements JsonSerializer<OfflinePlayer>, JsonDeserializer<OfflinePlayer> {

    @Override
    public JsonElement serialize(final OfflinePlayer player, final Type type, final JsonSerializationContext jsonSerializationContext) {
        final JsonObject object = new JsonObject();

        object.addProperty("offlinePlayerUUID", player.getUniqueId().toString());

        return object;
    }

    @Override
    public OfflinePlayer deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Bukkit.getOfflinePlayer(UUID.fromString(element.getAsJsonObject().get("offlinePlayerUUID").getAsString()));
    }
}
