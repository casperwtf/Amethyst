package wtf.casper.amethyst.core.utils.pastes;

import lombok.Setter;
import lombok.SneakyThrows;

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

public class Pastebin {

    private static final String PASTE_URL = "https://pastebin.com/api/api_post.php";
    @Setter
    private static String API_KEY = null;
    @Setter
    private static String USER_API_KEY = null;

    public static CompletableFuture<String> paste(String content, int privacyLevel) {
        return CompletableFuture.supplyAsync(() -> pasteSync(content, privacyLevel));
    }

    public static CompletableFuture<String> paste(String content) {
        return paste(content, 2);
    }

    @SneakyThrows
    private static String pasteSync(String content, int privacyLevel) {
        URL url = new URL(PASTE_URL);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;

        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setDoInput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("api_dev_key", API_KEY);
        arguments.put("api_paste_code", content);
        if (USER_API_KEY != null) {
            arguments.put("api_user_key", USER_API_KEY);
        }
        arguments.put("api_paste_private", privacyLevel + "");


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

        if (response.contains("private pastes for your free account")) {
            return pasteSync(content, 1);
        }

        if (response.contains("unlisted pastes for your free account")) {
            return pasteSync(content, 0);
        }

        if (response.startsWith("Bad API request")) {
            return null;
        }

        return response;
    }


}
