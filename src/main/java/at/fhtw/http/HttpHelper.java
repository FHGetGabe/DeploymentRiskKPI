package at.fhtw.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpHelper {

  // Proxy-Konfiguration (falls benötigt)
  private static final boolean USE_PROXY = true; // Proxy-Feature aktivieren/deaktivieren
  private static final String PROXY_HOST = "webgwooe.rbgooe.at"; // Proxy-Host
  private static final int PROXY_PORT = 8080; // Proxy-Port

  // HTTP Client - Konfiguration mit optionalem Proxy
  private static final HttpClient client = HttpClient.newBuilder()
                                                     .proxy(USE_PROXY ? ProxySelector.of(new InetSocketAddress(PROXY_HOST, PROXY_PORT)) : ProxySelector.getDefault())
                                                     .build();

  public enum Context {
    JIRA, DIGITAL_AI, DIGITAL_AI_ARCHIVE
  }

  private static final Map<Context, String> contextTokens = new HashMap<>();
  private static final Map<Context, String> contextAuthorization = new HashMap<>();
  private static final Map<Context, String> contextBaseUrls = new HashMap<>();

  static {
    contextTokens.put(Context.JIRA, "Bearer " + CredentialManager.getJiraAPIToken());
    contextAuthorization.put(Context.JIRA, "Authorization");
    contextBaseUrls.put(Context.JIRA, "https://collab.r-software.at/jira");

    contextTokens.put(Context.DIGITAL_AI, CredentialManager.getDigitalaiToken());
    contextAuthorization.put(Context.DIGITAL_AI, "x-release-personal-token");
    contextBaseUrls.put(Context.DIGITAL_AI, "https://xlrelease.rbgooe.at");

    contextTokens.put(Context.DIGITAL_AI_ARCHIVE, CredentialManager.getDigitalaiArchiveToken());
    contextAuthorization.put(Context.DIGITAL_AI_ARCHIVE, "x-release-personal-token");
    contextBaseUrls.put(Context.DIGITAL_AI_ARCHIVE, "https://xlrelease-archive-pre2023.rbgooe.at");
  }

  // GET-Request ausführen
  public static HttpResponse<String> get(String url, Context context) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
                                     .uri(URI.create(getBaseUrl(context) + url))
                                     .header(getAuthorization(context), getToken(context))
                                     .header("Accept", "application/json")
                                     .GET()
                                     .build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  // POST-Request ausführen
  public static HttpResponse<String> post(String url, Context context, String body) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
                                     .uri(URI.create(getBaseUrl(context) + url))
                                     .header(getAuthorization(context), getToken(context))
                                     .header("Accept", "application/json")
                                     .header("Content-Type", "application/json")
                                     .POST(HttpRequest.BodyPublishers.ofString(body))
                                     .build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  // Token für spezifischen Kontext abrufen
  private static String getToken(Context context) {
    return contextTokens.getOrDefault(context, "");
  }

  // Authorization-Type für spezifischen Kontext abrufen
  private static String getAuthorization(Context context) {
    return contextAuthorization.getOrDefault(context, "");
  }

  // Basis-URL für spezifischen Kontext abrufen
  private static String getBaseUrl(Context context) {
    return contextBaseUrls.getOrDefault(context, "");
  }
}