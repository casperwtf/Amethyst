package wtf.casper.amethyst.core.utils.pastes;

import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class Hastebin {

    private static final String HASTEBIN_URL = "https://hastebin.com/documents";

    public static CompletableFuture<String> paste(String content) {
        return CompletableFuture.supplyAsync(() -> pasteSync(content));
    }

    @SneakyThrows
    private static String pasteSync(String content) {
        final byte[] data = content.getBytes(StandardCharsets.UTF_8);
        final int length = data.length;

        final HttpsURLConnection connection = (HttpsURLConnection) new URL(HASTEBIN_URL).openConnection();

        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", String.valueOf(length));
        connection.setUseCaches(false);

        final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
        output.write(data);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String response = reader.readLine();
        if (response.contains("\"key\"")) {
            response = response.substring(response.indexOf(":") + 2, response.length() - 2);

            response = "https://hastebin.com/" + response;
        }

        return response;
    }

}
