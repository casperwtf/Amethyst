package wtf.casper.amethyst.minecraft.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MCUUIDToName implements Callable<String> {

    private final UUID uuid;

    public MCUUIDToName(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String call() {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""))).openConnection();
            JsonObject response = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

            String name = response.get("name").getAsString();

            if (name == null) {
                return "";
            }

            if (response.has("cause") && response.has("errorMessage")) {
                String cause = response.get("cause").getAsString();
                String errorMessage = response.get("errorMessage").getAsString();

                if (cause != null && cause.length() > 0) {
                    throw new IllegalStateException(errorMessage);
                }
            }

            return name;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get UUID", e);
        }

    }
}
