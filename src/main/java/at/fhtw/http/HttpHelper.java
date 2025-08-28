package at.fhtw.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHelper {

  private static final HttpClient client = HttpClient.newBuilder()
                                                     .build();

  public static HttpResponse<String> get (String url) throws IOException, InterruptedException {
    System.out.println(url);
    HttpRequest request = HttpRequest.newBuilder()
                                     .uri(URI.create(url))
                                     .header("Authorization", getToken())
                                     .header("Accept", "application/json")
                                     .GET()
                                     .build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private static String getToken () {
    String personalAccessToken = CredentialManager.getPersonalAccessToken(); // Fetch the token securely
    return "Bearer " + personalAccessToken;

  }
}
