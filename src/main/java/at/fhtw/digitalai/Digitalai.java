package at.fhtw.digitalai;

import at.fhtw.dataCollector.models.ReleaseXLRValues;
import at.fhtw.digitalai.models.GroupedReleases;
import at.fhtw.digitalai.models.Release;
import at.fhtw.digitalai.models.ReleaseCountResponse;
import at.fhtw.digitalai.models.SearchCriteria;
import at.fhtw.http.HttpHelper;
import at.fhtw.util.JsonUtils;
import at.fhtw.util.RoundUtil;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Digitalai {

  private static final String SEARCH_RELEASES_ENDPOINT = "/api/v1/releases/fullSearch?page=%d&resultsPerPage=%d&archivePage=%d&archiveResultsPerPage=%d";
  private static final String RELEASE_COUNT_ENDPOINT = "/api/v1/releases/count";


  public static Integer getReleaseCount (String releaseLabel,
                                         HttpHelper.Context context) throws IOException, InterruptedException {

    SearchCriteria searchCriteria = new SearchCriteria(List.of(releaseLabel, "rsg", "component"),
                                                       true);

    HttpResponse<String> httpResponse = HttpHelper.post(RELEASE_COUNT_ENDPOINT,
                                                        context,
                                                        JsonUtils.toString(searchCriteria));

    ReleaseCountResponse releaseCountResponse = JsonUtils.fromJson(httpResponse.body(),
                                                                   ReleaseCountResponse.class);

    return releaseCountResponse.getAll().getTotal();
  }

  public static ReleaseXLRValues getReleaseXLRValues (String releaseLabel,
                                                      Integer totalReleases,
                                                      HttpHelper.Context context) throws IOException, InterruptedException {

    SearchCriteria searchCriteria = new SearchCriteria(List.of(releaseLabel, "rsg", "component"),
                                                       true);
    List<Release> releases = new ArrayList<>();
    int page = 0;
    int maxResults = 100;
    int total = ((totalReleases + maxResults - 1) / maxResults);

    while (page < total) {
      String paginatedUrl = String.format(SEARCH_RELEASES_ENDPOINT,
                                          page,
                                          maxResults,
                                          page,
                                          maxResults);

      HttpResponse<String> httpResponse = HttpHelper.post(paginatedUrl,
                                                          context,
                                                          JsonUtils.toString(searchCriteria));

      GroupedReleases groupedReleases = JsonUtils.fromJson(httpResponse.body(),
                                                           GroupedReleases.class);
      releases.addAll(groupedReleases.getArchivedReleases().getReleases());
      releases.addAll(groupedReleases.getLiveReleases().getReleases());

      page += 1;
    }
    if (releases.isEmpty()) {
      return null;
    }

    double modifiedImplementation = DigitalaiData.getNumericMeanOfBooleanVariable(releases,
                                                                                  "modifiedImplementation");
    double modifiedConfiguration = DigitalaiData.getNumericMeanOfBooleanVariable(releases,
                                                                                 "modifiedConfiguration");
    double tooLate = DigitalaiData.getNumericMeanOfBooleanVariable(releases, "tooLate");
    double numberOfOperators = DigitalaiData.getMeanTrueBooleansForReleases(releases,
                                                                            List.of("installGRZ",
                                                                                    "installRIT",
                                                                                    "installRICS",
                                                                                    "installRZKtn",
                                                                                    "installRZVlbg"));
    double knownRiskIfNotDeployed = DigitalaiData.getNumericMeanOfStringVariable(releases,
                                                                                 "noDeploymentConsequences");

    return ReleaseXLRValues.builder()
                           .sonarStatus(RoundUtil.roundToSixDecimals(
                               DigitalaiData.evaluateSonarStatus(releases)))
                           .modifiedImplementation(RoundUtil.roundToSixDecimals(
                               modifiedImplementation))
                           .modifiedConfiguration(RoundUtil.roundToSixDecimals(
                               modifiedConfiguration))
                           .tooLateSoftwareTransfer(RoundUtil.roundToSixDecimals(
                               tooLate))
                           .numberOfOperators(RoundUtil.roundToSixDecimals(numberOfOperators))
                           .knownRiskIfNotDeployed(RoundUtil.roundToSixDecimals(
                               knownRiskIfNotDeployed))
                           .build();
  }
}
