package wtf.casper.amethyst.core.utils.pastes;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import wtf.casper.amethyst.core.AmethystCore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MCLogs {

    public static CompletableFuture<String> paste(String content) {
        return CompletableFuture.supplyAsync(() -> pasteSync(content));
    }

    @SneakyThrows
    private static String pasteSync(String content) {
        URL url = new URL("https://api.mclo.gs/1/log");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;

        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setDoInput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("content", content);


        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + StandardCharsets.UTF_8);
        http.connect();

        OutputStream os = http.getOutputStream();
        os.write(out);

        InputStream is = http.getInputStream();
        String response = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

        JsonObject jsonObject = AmethystCore.getGson().fromJson(response, JsonObject.class);
        if (jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
            return jsonObject.get("url").getAsString();
        } else {
            return null;
        }
    }
}
