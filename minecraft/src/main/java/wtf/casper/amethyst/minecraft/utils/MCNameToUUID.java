package wtf.casper.amethyst.minecraft.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MCNameToUUID implements Callable<UUID> {

    private final UUID uuid;

    public MCNameToUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID call() {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""))).openConnection();
            JsonObject response = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

            String id = response.get("id").getAsString();

            if (id == null) {
                return null;
            }

            if (response.has("cause") && response.has("errorMessage")) {
                String cause = response.get("cause").getAsString();
                String errorMessage = response.get("errorMessage").getAsString();

                if (cause != null && cause.length() > 0) {
                    throw new IllegalStateException(errorMessage);
                }
            }

            return UUID.fromString(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get UUID", e);
        }
    }
}
