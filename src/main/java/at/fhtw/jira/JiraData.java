package at.fhtw.jira;

import at.fhtw.jira.models.Application;
import at.fhtw.jira.models.CustomFieldOption;
import at.fhtw.jira.models.Fields;
import at.fhtw.jira.models.Issue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class JiraData {

  public static double getNumericCustomerAcceptanceRelevant (List<Issue> fieldsList) {
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
              .mapToDouble(JiraData::getCustomerAcceptanceRelevantAsDouble)
              .forEach(stats::addValue);

    return stats.getMean();
  }

  public static Double getCustomerAcceptanceRelevantAsDouble (String customerAcceptanceRelevant) {
    if ("Ja".equalsIgnoreCase(customerAcceptanceRelevant)) {
      return 1.0;
    }
    return 0.0;
  }

  public static double getIssueDependencyRatio(List<Issue> issues) {
    int count = issues.stream()
                      .filter(issue -> issue.getFields() != null && issue.getFields().getIssueLinks() != null)
                      .flatMap(issue -> issue.getFields()
                                             .getIssueLinks().stream()
                                             .filter(link -> link.getOutwardIssue() != null
                                                             && "depends on".equals(link.getType().getOutward()))
                      )
                      .mapToInt(link -> 1)
                      .sum();

    return (double)count / issues.size();
  }

  public static int getApplicationMatchSumWithDebug(List<Issue> issues) {
    return issues.stream()
                 .map(issue -> {
                   List<String> issueValues = Optional.ofNullable(issue.getFields())
                                                      .map(Fields::getApplication)
                                                      .orElse(List.of());

                   boolean isMatch = issueValues.stream()
                                                .anyMatch(issueValue -> Arrays.stream(Application.values())
                                                                              .anyMatch(app -> app.getValue().equals(issueValue)));
                   return isMatch ? 1 : 0;
                 })
                 .mapToInt(Integer::intValue)
                 .sum();
  }

  public static double getAverageResolutionTimeInDays(List<Issue> issues, LocalDate deploymentDate) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    if(issues.isEmpty()) {
      return 0;
    }


    issues.stream()
          .map(issue -> {
            String createdDateTime = Optional.ofNullable(issue.getFields())
                                            .map(Fields::getCreated)
                                            .orElse(null);
            String resolutionDateTime = Optional.ofNullable(issue.getFields())
                                                   .map(Fields::getResolutionDate)
                                                   .orElse(null);

            if (createdDateTime == null || resolutionDateTime == null) {
              System.out.println("Kein created oder resolutionDate Wert im Issue: " + issue.getKey());
              return null;
            }
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            LocalDate created =  OffsetDateTime.parse(createdDateTime, customFormatter)
                                               .toLocalDate();

            LocalDate resolutionDate =  OffsetDateTime.parse(resolutionDateTime, customFormatter)
                                                      .toLocalDate();

            if (!created.isBefore(deploymentDate)) {
              System.out.println("DeploymentDate vor dem CreatedDate im Issue: " + issue.getKey());
              return null;
            }

            if (!resolutionDate.isBefore(deploymentDate)) {
              System.out.println("DeploymentDate vor dem ResolutionDate im Issue: " + issue.getKey());
              return 999L;
            }

            return ChronoUnit.DAYS.between(created, resolutionDate);
          })
          .filter(Objects::nonNull)
          .mapToLong(Long::longValue)
          .forEach(stats::addValue);

    if (stats.getN() == 0) {
      return 0.0;
    }
    return stats.getMean();
  }

  public static double getTransformedDaysToDeployment(List<Issue> issues, LocalDate deploymentDate) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    if(issues.isEmpty()) {
      return 1.0;
    }

    issues.stream()
          .map(issue -> {
            String createdDateTime = Optional.ofNullable(issue.getFields())
                                            .map(Fields::getCreated)
                                            .orElse(null);

            if (createdDateTime == null) {
              System.out.println("Kein created Wert im Issue: " + issue.getKey());
              return null;
            }
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            LocalDate created =  OffsetDateTime.parse(createdDateTime, customFormatter)
                                               .toLocalDate();

            if (!created.isBefore(deploymentDate)) {
              System.out.println("DeploymentDate vor dem CreatedDate im Issue: " + issue.getKey());
              return null;
            }
            long daysBetween = ChronoUnit.DAYS.between(created, deploymentDate);

            return (1.0 / daysBetween);

          })
          .filter(Objects::nonNull)
          .mapToDouble(Double::doubleValue)
          .forEach(stats::addValue);

    if (stats.getN() == 0) {
      return 1.0;
    }
    return stats.getMean();
  }
}
