package at.fhtw.jira;

import at.fhtw.http.HttpHelper;
import at.fhtw.jira.data.SearchResult;
import at.fhtw.util.JsonUtils;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Jira {
    private final String jiraDomain = "your-domain.atlassian.net";
    private final String SEARCH_ENDPOINT = "/rest/api/3/search/jql?jql=release=%s";

    public Integer getIssueCount(String release) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = HttpHelper.get(jiraDomain + String.format(SEARCH_ENDPOINT, release));
        SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);
        return searchResult.getTotal();
    }
}
