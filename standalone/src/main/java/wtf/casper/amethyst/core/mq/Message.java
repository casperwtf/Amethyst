package wtf.casper.amethyst.core.mq;

import com.google.gson.JsonObject;
import lombok.Getter;
import wtf.casper.amethyst.core.AmethystCore;
import wtf.casper.amethyst.core.utils.AmethystLogger;

public interface Message {

    /**
     * We add in our own "amethyst_class_type" property to the JSON, so we can deserialize it later
     * This is to make sure that we don't have to use a type adapter for every single message
     * while still having the listener not listen to every message possible
     *
     * @return The message serialized to JSON
     */
    default String serialize() {
        String json = AmethystCore.getGson().toJson(this);
        JsonObject jsonObject = AmethystCore.getGson().fromJson(json, JsonObject.class);
        if (jsonObject == null) return json;
        if (jsonObject.has("amethyst_class_type")) {
            AmethystLogger.debug("Message " + this.getClass().getSimpleName() + " already has a property called \"amethyst_class_type\". This will cause issues with deserialization.");
            return json;
        }
        jsonObject.addProperty("amethyst_class_type", this.getClass().getSimpleName());
        return jsonObject.toString();
    }
}
