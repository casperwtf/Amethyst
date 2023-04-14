package wtf.casper.amethyst.paper.serialized;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class SerializablePlayerProfile {

    private final PlayerProfile playerProfile;

    public static SerializablePlayerProfile deserialize(JsonObject object) {
        String name = object.has("name") ? object.get("name").getAsString() : null;
        UUID uuid = object.has("uuid") ? UUID.fromString(object.get("uuid").getAsString()) : null;

        PlayerProfile profile;
        if (name != null && uuid != null) {
            profile = Bukkit.createProfile(uuid, name);
        } else if (name != null) {
            profile = Bukkit.createProfile(name);
        } else if (uuid != null) {
            profile = Bukkit.createProfile(uuid);
        } else {
            profile = Bukkit.createProfile(UUID.randomUUID(), null);
        }

        if (object.has("properties")) {
            JsonArray propertyArray = object.getAsJsonArray("properties");
            Set<ProfileProperty> properties = new HashSet<>();
            for (JsonElement rawPropertyObject : propertyArray) {
                JsonObject propertyObject = rawPropertyObject.getAsJsonObject();
                String propertyName = propertyObject.get("name").getAsString();
                String propertyValue = propertyObject.get("value").getAsString();
                String propertySignature = propertyObject.has("signature") ?
                        propertyObject.get("signature").getAsString() : null;

                ProfileProperty profileProperty;
                if (propertySignature != null) {
                    profileProperty = new ProfileProperty(propertyName, propertyValue, propertySignature);
                } else {
                    profileProperty = new ProfileProperty(propertyName, propertyValue);
                }

                properties.add(profileProperty);
            }

            profile.getProperties().addAll(properties);
        }

        return new SerializablePlayerProfile(profile);
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        String name = playerProfile.getName();
        if (name != null) {
            object.addProperty("name", name);
        }
        UUID uuid = playerProfile.getId();
        if (uuid != null) {
            object.addProperty("uuid", uuid.toString());
        }

        Set<ProfileProperty> profileProperties = playerProfile.getProperties();
        if (!profileProperties.isEmpty()) {
            JsonArray propertyArray = new JsonArray();
            for (ProfileProperty profileProperty : profileProperties) {
                JsonObject propertyObject = new JsonObject();
                propertyObject.addProperty("name", profileProperty.getName());
                propertyObject.addProperty("value", profileProperty.getValue());
                if (profileProperty.getSignature() != null) {
                    propertyObject.addProperty("signature", profileProperty.getSignature());
                }
                propertyArray.add(propertyObject);
            }
            object.add("properties", propertyArray);
        }

        return object;
    }

}
