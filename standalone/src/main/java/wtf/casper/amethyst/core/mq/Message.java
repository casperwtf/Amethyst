package wtf.casper.amethyst.core.mq;

import com.google.gson.JsonObject;
import lombok.extern.java.Log;
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
    default String jsonSerialize() {
        JsonObject jsonObject = AmethystCore.getGson().fromJson(AmethystCore.getGson().toJson(this), JsonObject.class);
        if (jsonObject == null) throw new NullPointerException("Failed to serialize message");
        jsonObject.addProperty("amethyst_class_type", this.getClass().getSimpleName());
        return jsonObject.toString();
    }
}
