package at.fhtw.digitalai.models;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SonarStatus {
  SONAR_STATUS_OK("sonar-status-ok", 0.0),
  SONAR_STATUS_ERROR("sonar-status-error", 1.0),
  SONAR_STATUS_VERSION_NOTFOUND("sonar-status-version_notfound", null),
  SONAR_STATUS_NA("sonar-status-na", null),
  SONAR_STATUS_NOBRANCHES("sonar-status-nobranches", null),
  SONAR_STATUS_BRANCH_NOTFOUND("sonar-status-branch_notfound", null),
  SONAR_STATUS_PROJECT_NOTFOUND("sonar-status-project_notfound", null);

  private final String value;
  private final Double numericValue;

  public static SonarStatus fromString (String value) {
    return Arrays.stream(SonarStatus.values())
                 .filter(status -> status.getValue().equalsIgnoreCase(value))
                 .findFirst()
                 .orElse(null);
  }


  public static SonarStatus findFirstMatchingStatus (List<String> tags) {
    List<SonarStatus> priorityOrder = List.of(
        SONAR_STATUS_OK,
        SONAR_STATUS_ERROR,
        SONAR_STATUS_VERSION_NOTFOUND,
        SONAR_STATUS_NA,
        SONAR_STATUS_NOBRANCHES,
        SONAR_STATUS_BRANCH_NOTFOUND,
        SONAR_STATUS_PROJECT_NOTFOUND
    );

    return priorityOrder.stream()
                        .filter(priorityStatus -> tags.stream()
                                                      .map(SonarStatus::fromString)
                                                      .anyMatch(tagStatus -> tagStatus == priorityStatus))
                        .findFirst()
                        .orElse(null);
  }
}