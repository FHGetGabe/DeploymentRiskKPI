package at.fhtw.jira;

import at.fhtw.http.HttpHelper;
import at.fhtw.jira.data.SearchResult;
import at.fhtw.util.JsonUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Jira {
    private static final String jiraDomain = "https://collab.r-software.at/jira";
    private static final String SEARCH_ENDPOINT = "/rest/api/2/search?jql=%s";

    public static Integer getIssueCount(String release) throws IOException, InterruptedException {
        String releaseKey = String.format("issuetype = Story AND Target-Release = %s", release);
        String encoded = URLEncoder.encode(releaseKey, StandardCharsets.UTF_8);
        HttpResponse<String> httpResponse = HttpHelper.get(jiraDomain + String.format(SEARCH_ENDPOINT, encoded));
        SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);
        return searchResult.getTotal();
    }

    public static void main (String[] args) throws IOException, InterruptedException {
        Integer count = Jira.getIssueCount("RM-2539762");
        System.out.println(count);
    }
}
