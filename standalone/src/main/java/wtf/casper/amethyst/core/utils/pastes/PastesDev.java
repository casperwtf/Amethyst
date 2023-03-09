package wtf.casper.amethyst.core.utils.pastes;

import com.google.gson.JsonObject;
import wtf.casper.amethyst.core.AmethystCore;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PastesDev {

    public static CompletableFuture<String> paste(String content) {
        return CompletableFuture.supplyAsync(() -> pasteSync(content));
    }

    private static String pasteSync(String content) {
        URL url;
        try {
            url = new URL("https://api.pastes.dev/post");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        URLConnection con;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection http = (HttpURLConnection) con;

        try {
            http.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        http.setDoOutput(true);
        http.setDoInput(true);

        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        http.setFixedLengthStreamingMode(contentBytes.length);
        http.setRequestProperty("Content-Type", "text/plain");
        http.setRequestProperty("Content-Length", String.valueOf(contentBytes.length));
        http.setRequestProperty("User-Agent", "Amethyst");
        http.setRequestProperty("Accept", "*/*");

        try {
            http.connect();
        } catch (Exception e) {
            return null;
        }

        try {
            OutputStream os = http.getOutputStream();
            os.write(contentBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        InputStream is;
        try {
            is = http.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

        JsonObject jsonObject = AmethystCore.getGson().fromJson(response, JsonObject.class);
        if (jsonObject.has("key")) {
            return "https://pastes.dev/" + jsonObject.get("key").getAsString();
        } else {
            return null;
        }

    }

}
