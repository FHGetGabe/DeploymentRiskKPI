package at.fhtw.jira;

import at.fhtw.jira.models.Application;
import at.fhtw.jira.models.CustomFieldOption;
import at.fhtw.jira.models.Fields;
import at.fhtw.jira.models.Issue;
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
}
