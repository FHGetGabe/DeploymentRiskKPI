package at.fhtw.digitalai;

import at.fhtw.dataCollector.models.ReleaseXLRValues;
import at.fhtw.digitalai.models.GroupedReleases;
import at.fhtw.digitalai.models.Release;
import at.fhtw.digitalai.models.ReleaseCountResponse;
import at.fhtw.digitalai.models.SearchCriteria;
import at.fhtw.digitalai.models.SonarStatus;
import at.fhtw.digitalai.models.Variable;
import at.fhtw.http.HttpHelper;
import at.fhtw.util.JsonUtils;
import at.fhtw.util.RoundUtil;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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

    double modifiedImplementation = getNumericMeanOfVariable(releases, "modifiedImplementation");
    double modifiedConfiguration = getNumericMeanOfVariable(releases, "modifiedConfiguration");
    double tooLate = getNumericMeanOfVariable(releases, "tooLate");

    return ReleaseXLRValues.builder()
                           .sonarStatus(RoundUtil.roundToSixDecimals(
                               evaluateSonarStatus(releases)))
                           .modifiedImplementation(RoundUtil.roundToSixDecimals(
                               modifiedImplementation))
                           .modifiedConfiguration(RoundUtil.roundToSixDecimals(
                               modifiedConfiguration))
                           .tooLateSoftwareTransfer(RoundUtil.roundToSixDecimals(
                               tooLate))
                           .build();
  }

  public static double evaluateSonarStatus (List<Release> releases) {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    releases.forEach(release -> {
      SonarStatus status = SonarStatus.findFirstMatchingStatus(release.getTags());

      if (status != null && status.getNumericValue() != null) {
        stats.addValue(status.getNumericValue());
      }
      if (status == null) {
        stats.addValue(1.0);
      }
    });

    return stats.getMean();
  }

  private static double getNumericMeanOfVariable (List<Release> releases,
                                                  String variableName) {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    releases.stream()
            .map(release -> {
              return Optional.ofNullable(release.getVariables())
                             .flatMap(variables -> variables.stream()
                                                            .filter(variable -> variableName.equals(
                                                                variable.getKey()))
                                                            .findFirst())
                             .map(Variable::getValue)
                             .map(value -> {
                               if (value instanceof Boolean) {
                                 return ((Boolean)value) ? 1.0 : 0.0;
                               }
                               return null;
                             })
                             .orElseGet(() -> {
                               System.out.println("Variable mit Key '" + variableName + "' in Release'" + release.getTitle() + "' nicht gefunden.");
                               return null;
                             });
            })
            .filter(Objects::nonNull)
            .forEach(stats::addValue);

    return stats.getMean();
  }

}
