package at.fhtw.dataCollector;

import at.fhtw.dataCollector.models.CSVParameter;
import at.fhtw.dataCollector.models.ReleaseXLRValues;
import at.fhtw.digitalai.Digitalai;
import at.fhtw.http.HttpHelper;
import at.fhtw.jira.Jira;
import at.fhtw.jira.models.ObjectEntry;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCollector {

  public static void main (String[] args) throws IOException, InterruptedException {

    List<ObjectEntry> hauptReleasesObjectEntries = Jira.getHauptReleasesObjectEntries();
    AtomicInteger index = new AtomicInteger(1);

    hauptReleasesObjectEntries.forEach(objectEntry -> {
      try {
        System.out.println("Processing release " + index.getAndIncrement() + ": " + objectEntry.getLabel());
        getValuesForRelease(objectEntry);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    //csvParameters.writeToCSV("src/main/resources/stories.csv");

  }

  private static void getValuesForRelease (ObjectEntry objectEntry) throws IOException, InterruptedException {

    Integer storyCount = Jira.getStoryCount(objectEntry.getObjectKey());
    Integer defectCount = Jira.getDefectCount(objectEntry.getObjectKey());

    Integer releaseCountDigitalAi = Digitalai.getReleaseCount(objectEntry.getLabel(),
                                                              HttpHelper.Context.DIGITAL_AI);
    Integer releaseCountDigitalAiArchive = Digitalai.getReleaseCount(objectEntry.getLabel(),
                                                                     HttpHelper.Context.DIGITAL_AI_ARCHIVE);
    Integer totalReleaseCount = releaseCountDigitalAi + releaseCountDigitalAiArchive;
    System.out.println("Total Release Count: " + totalReleaseCount);
    ReleaseXLRValues releaseXLRValues = Digitalai.getReleaseXLRValues(objectEntry.getLabel(),
                                                                      releaseCountDigitalAi,
                                                                      HttpHelper.Context.DIGITAL_AI);
    ReleaseXLRValues releaseXLRArchiveValues = Digitalai.getReleaseXLRValues(objectEntry.getLabel(),
                                                                             releaseCountDigitalAiArchive,
                                                                             HttpHelper.Context.DIGITAL_AI_ARCHIVE);

    ReleaseXLRValues totalReleaseXLRValues = getTotalReleaseXLRValues(releaseXLRValues,
                                                                      releaseXLRArchiveValues);
    //ReleaseStoryValues releaseStoryValues = Jira.getReleaseStoryValues(objectEntry.getObjectKey());

    CSVParameter csvParameter = CSVParameter.builder()
                                            .storyCount(storyCount)
                                            .defectCount(defectCount)
                                            .releaseXLRValues(totalReleaseXLRValues)
                                            .releaseCount(totalReleaseCount)
                                            .build();

    csvParameter.writeToCSV("src/main/resources/stories.csv");
  }

  private static ReleaseXLRValues getTotalReleaseXLRValues (ReleaseXLRValues releaseXLRValues,
                                                            ReleaseXLRValues releaseXLRArchiveValues) {
    if (releaseXLRValues == null) {
      return releaseXLRArchiveValues;
    }
    if (releaseXLRArchiveValues == null) {
      return releaseXLRValues;
    }
    return releaseXLRValues.plus(releaseXLRArchiveValues);
  }

  public static void main1 (String[] args) throws IOException, InterruptedException {
    getValuesForRelease(new ObjectEntry("RM-2539762", "250717R"));
  }

}

