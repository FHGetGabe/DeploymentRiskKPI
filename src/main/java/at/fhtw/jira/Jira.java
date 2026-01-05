package at.fhtw.jira;

import at.fhtw.dataCollector.models.DefectValueSum;
import at.fhtw.dataCollector.models.ReleaseStoryValues;
import at.fhtw.http.HttpHelper;
import at.fhtw.jira.models.AssetsData;
import at.fhtw.jira.models.DefectStatValue;
import at.fhtw.jira.models.DefectValues;
import at.fhtw.jira.models.Issue;
import at.fhtw.jira.models.ObjectEntry;
import at.fhtw.jira.models.SearchResult;
import at.fhtw.util.JsonUtils;
import at.fhtw.util.RoundUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Jira {

  private static final String SEARCH_ENDPOINT_PAGINATED = "/rest/api/2/search?jql=%s&startAt=%d&maxResults=%d";
  private static final String ASSETS_SEARCH_ENDPOINT = "/rest/insight/1.0/iql/objects?iql=%s&resultPerPage=300";

  public static List<ObjectEntry> getHauptReleasesObjectEntries () throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(
        "\"Gelöscht\" = False AND \"Name\" ENDSWITH R OR \"Name\" ENDSWITH G AND \"Zweiter Einsatztag in Produktion\" < now() AND \"objectType\" = Hauptrelease AND \"Installation KAZ\" > 14.08.2020",
        StandardCharsets.UTF_8);

    HttpResponse<String> httpResponse = HttpHelper.get(String.format(
        ASSETS_SEARCH_ENDPOINT, encoded), HttpHelper.Context.JIRA);

    AssetsData assetsData = JsonUtils.fromJson(httpResponse.body(), AssetsData.class);

    return assetsData.getObjectEntries();
  }

  public static List<ObjectEntry> getSonderReleasesObjectEntries () throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(
        "\"Gelöscht\" = False AND \"Name\" ENDSWITH M OR \"Name\" ENDSWITH G OR \"Name\" ENDSWITH S OR \"Name\" ENDSWITH U OR \"Name\" ENDSWITH P OR \"Name\" ENDSWITH T AND \"Zweiter Einsatztag in Produktion\" < now() AND \"objectType\" = Sonderrelease AND \"Installation KAZ\" > 14.08.2020",
        StandardCharsets.UTF_8);

    HttpResponse<String> httpResponse = HttpHelper.get(String.format(
        ASSETS_SEARCH_ENDPOINT, encoded), HttpHelper.Context.JIRA);

    AssetsData assetsData = JsonUtils.fromJson(httpResponse.body(), AssetsData.class);

    return assetsData.getObjectEntries();
  }

  public static List<ObjectEntry> getHotfixReleasesObjectEntries () throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(
        "\"Gelöscht\" = False AND \"Name\" ENDSWITH H AND \"Zweiter Einsatztag in Produktion\" < now() AND \"objectType\" = Hotfix AND \"Erster Einsatztag in Produktion\" > 14.08.2020",
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

  public static Integer getTotalDefectCount (String releaseId) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format("issuetype = Defect AND Detected-Release = %s",
                                                     releaseId), StandardCharsets.UTF_8);
    return getIssueCount(encoded);
  }

  public static Integer getSpecificDefectCount (String releaseId,
                                                DefectValues criticality,
                                                DefectValues foundIn) throws IOException, InterruptedException {
    String encoded = URLEncoder.encode(String.format(
        "issuetype = Defect AND Detected-Release = %s AND Kritikalität = %s AND \"Gefunden in\" = %s",
        releaseId,
        criticality.getValue(),
        foundIn.getValue()), StandardCharsets.UTF_8);
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
    List<Issue> issues = getIssues(baseEncodedQuery);

    return ReleaseStoryValues.builder()
                             .numericCustomerAcceptanceRelevant(RoundUtil.roundToSixDecimals(
                                 JiraData.getNumericCustomerAcceptanceRelevant(
                                     issues)))
                             .issueDependencyRatio(JiraData.getIssueDependencyRatio(issues))
                             .criticalIssueCount(JiraData.getApplicationMatchSumWithDebug(issues))
                             .build();
  }

  private static List<Issue> getIssues (String baseEncodedQuery) throws IOException, InterruptedException {
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
    return issues;
  }

  public static DefectStatValue getDefectStatValues (String releaseId, LocalDate deploymentDate) throws IOException, InterruptedException {
    String baseEncodedQuery = URLEncoder.encode(String.format(
        "issuetype = Defect AND Detected-Release = %s AND (\"Gefunden in\" = Test OR \"Gefunden in\" = Kundenabnahme)",
        releaseId), StandardCharsets.UTF_8);
    List<Issue> issues = getIssues(baseEncodedQuery);

    return DefectStatValue.builder()
                          .averageResolutionTimeInDays(JiraData.getAverageResolutionTimeInDays(
                              issues))
        .transformedDaysToDeployment(JiraData.getTransformedDaysToDeployment(issues, deploymentDate))
                          .build();
  }

  public static DefectValueSum getFoundInDefectCount (String releaseId,
                                                      DefectValues foundIn) throws IOException, InterruptedException {
    Map<DefectValues, Integer> foundInMap = new HashMap<>();
    List<DefectValues> criticalityValues = List.of(DefectValues.MO,
                                                   DefectValues.M1,
                                                   DefectValues.M2,
                                                   DefectValues.M3);
    for (DefectValues criticality : criticalityValues) {
      try {
        int count = getSpecificDefectCount(releaseId, criticality, foundIn);
        foundInMap.put(criticality, count);
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException("Error while fetching defect count for criticality: " + criticality,
                                   e);
      }
    }

    int totalSum = foundInMap.values().stream().mapToInt(Integer::intValue).sum();

    int weightedSum = foundInMap.entrySet().stream()
                                .mapToInt(entry -> {
                                  int weight = getCriticalityWeight(entry.getKey());
                                  return entry.getValue() * weight;
                                })
                                .sum();

    return DefectValueSum
        .builder()
        .totalSum(totalSum)
        .weightedSum(weightedSum)
        .totalM0Sum(foundInMap.get(DefectValues.MO))
        .totalM1Sum(foundInMap.get(DefectValues.M1))
        .totalM2Sum(foundInMap.get(DefectValues.M2))
        .totalM3Sum(foundInMap.get(DefectValues.M3))
        .build();
  }

  private static int getCriticalityWeight (DefectValues criticality) {
    return switch (criticality) {
      case MO -> 180;
      case M1 -> 90;
      case M2 -> 20;
      case M3 -> 1;
      default -> 0;
    };
  }
}
