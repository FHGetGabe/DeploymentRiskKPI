package at.fhtw.jira;

import at.fhtw.http.HttpHelper;
import at.fhtw.jira.models.AssetsData;
import at.fhtw.jira.models.Issue;
import at.fhtw.jira.models.ObjectEntry;
import at.fhtw.jira.models.ReleaseEffortValues;
import at.fhtw.jira.models.SearchResult;
import at.fhtw.util.JsonUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Jira {

  private static final String jiraDomain = "https://collab.r-software.at/jira";
  private static final String SEARCH_ENDPOINT_PAGINATED = "/rest/api/2/search?jql=%s&startAt=%d&maxResults=%d";
  private static final String ASSETS_SEARCH_ENDPOINT = "/rest/insight/1.0/iql/objects?iql=%s&resultPerPage=100";

  public static List<String> getHauptReleasesKey () throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(
        "\"Gelöscht\" = False AND \"Name\" ENDSWITH R AND \"Zweiter Einsatztag in Produktion\" < now() AND \"objectType\" = Hauptrelease",
        StandardCharsets.UTF_8);

    HttpResponse<String> httpResponse = HttpHelper.get(jiraDomain + String.format(
        ASSETS_SEARCH_ENDPOINT, encoded));

    AssetsData assetsData = JsonUtils.fromJson(httpResponse.body(), AssetsData.class);

    return assetsData.getObjectEntries()
                     .stream()
                     .map(ObjectEntry::getObjectKey)
                     .collect(Collectors.toList());

  }

  public static Integer getStoryCount (String release) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format("issuetype = Story AND Target-Release = %s",
                                                     release), StandardCharsets.UTF_8);
    return getIssueCount(encoded);
  }

  public static Integer getDefectCount (String release) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format("issuetype = Defect AND Detected-Release = %s",
                                                     release), StandardCharsets.UTF_8);
    return getIssueCount(encoded);
  }

  private static Integer getIssueCount (String encoded) throws IOException, InterruptedException {
    HttpResponse<String> httpResponse = HttpHelper.get(jiraDomain + String.format(
        SEARCH_ENDPOINT_PAGINATED, encoded, 0, 1));

    SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);

    return searchResult.getTotal();
  }

  public static ReleaseEffortValues getReleaseEffortValues (String release) throws IOException, InterruptedException {
    String baseEncodedQuery = URLEncoder.encode(String.format(
        "issuetype = Story AND Target-Release = %s",
        release), StandardCharsets.UTF_8);
    DescriptiveStatistics storyPointsStats = new DescriptiveStatistics();
    DescriptiveStatistics ptStats = new DescriptiveStatistics();

    int startAt = 0;
    int maxResults = 100;
    int total;

    do {
      String paginatedUrl = jiraDomain + String.format(SEARCH_ENDPOINT_PAGINATED,
                                                       baseEncodedQuery,
                                                       startAt,
                                                       maxResults);

      HttpResponse<String> httpResponse = HttpHelper.get(paginatedUrl);

      SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);
      searchResult.getIssues().stream()
                  .map(Issue::getFields)
                  .forEach(fields -> {
                    processEffort(fields.getEffortEstimationInStoryPoints(), storyPointsStats);
                    processEffort(fields.getEffortEstimationInPT(), ptStats);
                  });

      total = searchResult.getTotal();
      startAt += maxResults;

    } while (startAt < total);


    DescriptiveStatistics storyPointsFilteredStats = filterOutliers(storyPointsStats);
    DescriptiveStatistics ptFilteredStats = filterOutliers(ptStats);

    return ReleaseEffortValues.builder()
                              .storyPointsTotal(roundToTwoDecimals(storyPointsFilteredStats.getSum()))
                              .storyPointsAverage(roundToTwoDecimals(storyPointsFilteredStats.getMean()))
                              .storyPointsMedian(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(50)))
                              .storyPoints25Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(25)))
                              .storyPoints35Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(35)))
                              .storyPoints45Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(45)))
                              .storyPoints55Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(55)))
                              .storyPoints65Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(65)))
                              .storyPoints75Percentile(roundToTwoDecimals(storyPointsFilteredStats.getPercentile(75)))
                              .ptTotal(roundToTwoDecimals(ptFilteredStats.getSum()))
                              .ptAverage(roundToTwoDecimals(ptFilteredStats.getMean()))
                              .ptMedian(roundToTwoDecimals(ptFilteredStats.getPercentile(50)))
                              .pt25Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(25)))
                              .pt35Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(35)))
                              .pt45Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(45)))
                              .pt55Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(55)))
                              .pt65Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(65)))
                              .pt75Percentile(roundToTwoDecimals(ptFilteredStats.getPercentile(75)))
                              .build();
  }

  public static DescriptiveStatistics filterOutliers(DescriptiveStatistics stats) {
    double upperThreshold = stats.getPercentile(75) + 3 * (stats.getPercentile(75) - stats.getPercentile(25));

    DescriptiveStatistics filteredStats = new DescriptiveStatistics();
    Arrays.stream(stats.getValues())
          .filter(value -> value <= upperThreshold)
          .forEach(filteredStats::addValue);

    return filteredStats;
  }


  private static void processEffort(Double effort, DescriptiveStatistics stats) {
    if (effort != null && effort > 0) {
      stats.addValue(effort);
    }
  }

  private static double roundToTwoDecimals (double value) {
    return Math.round(value * 100.0) / 100.0;
  }
}
