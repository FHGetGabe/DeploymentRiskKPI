package at.fhtw.regression.trainer;

import at.fhtw.jira.Jira;
import at.fhtw.jira.models.ReleaseEffortValues;
import at.fhtw.regression.trainer.models.CSVParameter;
import at.fhtw.regression.trainer.models.CSVParameters;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCollector {

  public static void main (String[] args) throws IOException, InterruptedException {

    List<String> hauptReleasesKey = Jira.getHauptReleasesKey();
    CSVParameters csvParameters = new CSVParameters();
    AtomicInteger index = new AtomicInteger(1);

    hauptReleasesKey.forEach(releaseKey -> {
      try {
        System.out.println("Processing release " + index.getAndIncrement() + ": " + releaseKey);
        getValuesForRelease(releaseKey, csvParameters);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    csvParameters.writeToCSV("src/main/resources/stories.csv");

  }

  private static void getValuesForRelease (String releaseKey,
                                           CSVParameters csvParameters) throws IOException, InterruptedException {
    Integer storyCount = Jira.getStoryCount(releaseKey);
    Integer defectCount = Jira.getDefectCount(releaseKey);
    ReleaseEffortValues releaseEffortValues = Jira.getReleaseEffortValues(releaseKey);

    CSVParameter csvParameter = CSVParameter.from(releaseEffortValues, storyCount, defectCount);
    csvParameters.addToCSVParameter(csvParameter);
  }
}
