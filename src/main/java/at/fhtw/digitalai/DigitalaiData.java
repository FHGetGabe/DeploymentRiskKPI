package at.fhtw.digitalai;

import at.fhtw.digitalai.models.Release;
import at.fhtw.digitalai.models.SonarStatus;
import at.fhtw.digitalai.models.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DigitalaiData {

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

  public static double getNumericMeanOfBooleanVariable (List<Release> releases,
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

  public static double getMeanTrueBooleansForReleases (List<Release> releases,
                                                       List<String> variableNames) {
    DescriptiveStatistics stats = new DescriptiveStatistics();

    releases.forEach(release -> {
      List<String> missingVariables = new ArrayList<>(variableNames);

      int sum = Optional.ofNullable(release.getVariables())
                        .map(variables -> variables.stream()
                                                   .filter(variable -> {
                                                     boolean found = variableNames.contains(variable.getKey());
                                                     if (found) {
                                                       missingVariables.remove(variable.getKey());
                                                     }
                                                     return found;
                                                   })
                                                   .map(Variable::getValue)
                                                   .filter(value -> value instanceof Boolean && (Boolean)value)
                                                   .mapToInt(value -> 1)
                                                   .sum())
                        .orElse(0);

      if (!missingVariables.isEmpty()) {
        System.out.println("Die folgenden Variablen wurden in Release '" + release.getTitle() + "' nicht gefunden: " + missingVariables);
      }

      stats.addValue(sum);
    });

    return stats.getMean();
  }

  public static double getNumericMeanOfStringVariable (List<Release> releases,
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
                               System.out.println("Value: [" + value + "]");
                               if (value instanceof String stringValue) {
                                 return !stringValue.isEmpty() ? 1.0 : 0.0;
                               }
                               System.out.println("Variable mit Key '" + variableName + "' in Release '" + release.getTitle() + "' nicht gefunden.");
                               return null;
                             })
                             .orElseGet(() -> 0.0);
            })
            .forEach(stats::addValue);

    return stats.getMean();
  }
}
