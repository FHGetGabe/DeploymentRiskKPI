package at.fhtw.jira;

import at.fhtw.dataCollector.models.ReleaseStoryValues;
import at.fhtw.http.HttpHelper;
import at.fhtw.jira.models.AssetsData;
import at.fhtw.jira.models.CustomFieldOption;
import at.fhtw.jira.models.Fields;
import at.fhtw.jira.models.Issue;
import at.fhtw.jira.models.ObjectEntry;
import at.fhtw.jira.models.SearchResult;
import at.fhtw.util.JsonUtils;
import at.fhtw.util.RoundUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Jira {

  private static final String SEARCH_ENDPOINT_PAGINATED = "/rest/api/2/search?jql=%s&startAt=%d&maxResults=%d";
  private static final String ASSETS_SEARCH_ENDPOINT = "/rest/insight/1.0/iql/objects?iql=%s&resultPerPage=100";

  public static List<ObjectEntry> getHauptReleasesObjectEntries () throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(
        "\"Gelöscht\" = False AND \"Name\" ENDSWITH R AND \"Zweiter Einsatztag in Produktion\" < now() AND \"objectType\" = Hauptrelease AND \"Installation KAZ\" > 14.08.2020",
        StandardCharsets.UTF_8);

    HttpResponse<String> httpResponse = HttpHelper.get(String.format(
        ASSETS_SEARCH_ENDPOINT, encoded), HttpHelper.Context.JIRA);

    AssetsData assetsData = JsonUtils.fromJson(httpResponse.body(), AssetsData.class);

    return assetsData.getObjectEntries();
  }

  public static Integer getStoryCount (String releaseId) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format("issuetype = Story AND Target-Release = %s",
                                                     releaseId), StandardCharsets.UTF_8);
    return getIssueCount(encoded);
  }

  public static Integer getDefectCount (String releaseId) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format("issuetype = Defect AND Detected-Release = %s",
                                                     releaseId), StandardCharsets.UTF_8);
    return getIssueCount(encoded);
  }

  private static Integer getIssueCount (String encoded) throws IOException, InterruptedException {
    HttpResponse<String> httpResponse = HttpHelper.get(String.format(
        SEARCH_ENDPOINT_PAGINATED, encoded, 0, 1), HttpHelper.Context.JIRA);

    SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);

    return searchResult.getTotal();
  }

  public static ReleaseStoryValues getReleaseStoryValues (String releaseId) throws IOException, InterruptedException {
    String baseEncodedQuery = URLEncoder.encode(String.format(
        "issuetype = Story AND Target-Release = %s",
        releaseId), StandardCharsets.UTF_8);
    DescriptiveStatistics storyPointsStats = new DescriptiveStatistics();
    DescriptiveStatistics ptStats = new DescriptiveStatistics();
    List<Issue> issues = new ArrayList<>();

    int startAt = 0;
    int maxResults = 100;
    int total;

    do {
      String paginatedUrl = String.format(SEARCH_ENDPOINT_PAGINATED,
                                          baseEncodedQuery,
                                          startAt,
                                          maxResults);

      HttpResponse<String> httpResponse = HttpHelper.get(paginatedUrl, HttpHelper.Context.JIRA);

      SearchResult searchResult = JsonUtils.fromJson(httpResponse.body(), SearchResult.class);
      issues.addAll(searchResult.getIssues());

      total = searchResult.getTotal();
      startAt += maxResults;

    } while (startAt < total);

    issues.stream()
          .map(Issue::getFields)
          .forEach(fields -> {
            processEffort(fields.getEffortEstimationInStoryPoints(), storyPointsStats);
            processEffort(fields.getEffortEstimationInPT(), ptStats);
          });

    DescriptiveStatistics storyPointsFilteredStats = filterOutliers(storyPointsStats);
    DescriptiveStatistics ptFilteredStats = filterOutliers(ptStats);

    return ReleaseStoryValues.builder()
                             .storyPointsTotal(RoundUtil.roundToSixDecimals(storyPointsFilteredStats.getSum()))
                             .storyPointsAverage(RoundUtil.roundToSixDecimals(
                                 storyPointsFilteredStats.getMean()))
                             .storyPointsMedian(RoundUtil.roundToSixDecimals(
                                 storyPointsFilteredStats.getPercentile(
                                     50)))
                             .ptTotal(RoundUtil.roundToSixDecimals(ptFilteredStats.getSum()))
                             .ptAverage(RoundUtil.roundToSixDecimals(ptFilteredStats.getMean()))
                             .ptMedian(RoundUtil.roundToSixDecimals(ptFilteredStats.getPercentile(50)))
                             .numericCustomerAcceptanceRelevant(RoundUtil.roundToSixDecimals(
                                 getNumericCustomerAcceptanceRelevant(
                                     issues)))
                             .build();
  }

  public static DescriptiveStatistics filterOutliers (DescriptiveStatistics stats) {
    double upperThreshold = stats.getPercentile(75) + 3 * (stats.getPercentile(75) - stats.getPercentile(
        25));

    DescriptiveStatistics filteredStats = new DescriptiveStatistics();
    Arrays.stream(stats.getValues())
          .filter(value -> value <= upperThreshold)
          .forEach(filteredStats::addValue);

    return filteredStats;
  }

  private static void processEffort (Double effort,
                                     DescriptiveStatistics stats) {
    if (effort != null && effort > 0) {
      stats.addValue(effort);
    }
  }

  private static double getNumericCustomerAcceptanceRelevant (List<Issue> fieldsList) {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    fieldsList.stream()
              .map(issue -> {
                String value = Optional.ofNullable(issue.getFields())
                                       .map(Fields::getCustomerAcceptanceRelevant)
                                       .map(CustomFieldOption::getValue)
                                       .orElse(null);
                if (value == null) {
                  System.out.println("Kein Customer Acceptance Wert im Issue: " + issue.getKey());
                }
                return value;
              })
              .filter(Objects::nonNull)
              .mapToDouble(Jira::getCustomerAcceptanceRelevantAsDouble)
              .forEach(stats::addValue);

    return stats.getMean();
  }

  public static Double getCustomerAcceptanceRelevantAsDouble (String customerAcceptanceRelevant) {
    if ("Ja".equalsIgnoreCase(customerAcceptanceRelevant)) {
      return 1.0;
    }
    return 0.0;
  }
}
