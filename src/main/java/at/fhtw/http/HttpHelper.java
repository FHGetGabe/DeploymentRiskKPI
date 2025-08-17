package at.fhtw.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class HttpHelper {

    private static final HttpClient client = HttpClient.newBuilder()
            .build();

    public static HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", getBasicAuthHeader())
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String getBasicAuthHeader() {
        String username = CredentialManager.getUsername();
        String password = CredentialManager.getPassword();
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encoded;
    }
}
