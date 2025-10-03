package at.fhtw.dataCollector;

import at.fhtw.dataCollector.models.CSVParameter;
import at.fhtw.dataCollector.models.ReleaseStoryValues;
import at.fhtw.dataCollector.models.ReleaseXLRValues;
import at.fhtw.digitalai.Digitalai;
import at.fhtw.http.HttpHelper;
import at.fhtw.jira.Jira;
import at.fhtw.jira.models.ObjectEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCollector {

  public static void main (String[] args) throws IOException, InterruptedException {

    //List<ObjectEntry> hauptReleasesObjectEntries = Jira.getHauptReleasesObjectEntries();
    List<ObjectEntry> sonderReleasesObjectEntries = Jira.getSonderReleasesObjectEntries();
    List<ObjectEntry> hotfixReleasesObjectEntries = Jira.getHotfixReleasesObjectEntries();

    //List<ObjectEntry> allReleases = new ArrayList<>(hauptReleasesObjectEntries);
    //allReleases.addAll(sonderReleasesObjectEntries);
    List<ObjectEntry> allReleases = new ArrayList<>(sonderReleasesObjectEntries);
    allReleases.addAll(hotfixReleasesObjectEntries);

    AtomicInteger index = new AtomicInteger(1);
    System.out.println("Found " + sonderReleasesObjectEntries.size() + " Sonderreleases.");
    System.out.println("Found " + hotfixReleasesObjectEntries.size() + " Hotfixreleases.");


    allReleases.forEach(objectEntry -> {
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

    ReleaseStoryValues releaseStoryValues = Jira.getReleaseStoryValues(objectEntry.getObjectKey());

    Integer totalReleaseCount = releaseCountDigitalAi + releaseCountDigitalAiArchive;
    System.out.println("Total Release Count: " + totalReleaseCount);
    ReleaseXLRValues releaseXLRValues = Digitalai.getReleaseXLRValues(objectEntry.getLabel(),
                                                                      releaseCountDigitalAi,
                                                                      HttpHelper.Context.DIGITAL_AI);
    ReleaseXLRValues releaseXLRArchiveValues = Digitalai.getReleaseXLRValues(objectEntry.getLabel(),
                                                                             releaseCountDigitalAiArchive,
                                                                             HttpHelper.Context.DIGITAL_AI_ARCHIVE);

    ReleaseXLRValues totalReleaseXLRValues = ReleaseXLRValues.getTotalReleaseXLRValues(
        releaseXLRValues,
        releaseXLRArchiveValues,
        releaseCountDigitalAi,
        releaseCountDigitalAiArchive);

    if(totalReleaseCount == 0 || storyCount == 0) {
      System.out.println("Skip Release: " + objectEntry.getLabel());
      return;
    }

    CSVParameter csvParameter = CSVParameter.builder()
                                            .releaseStoryValues(releaseStoryValues)
                                            .storyCount(storyCount)
                                            .defectCount(defectCount)
                                            .releaseXLRValues(totalReleaseXLRValues)
                                            .releaseCount(totalReleaseCount)
                                            .build();

    csvParameter.writeToCSV("src/main/resources/stories.csv");
  }

  public static void main1 (String[] args) throws IOException, InterruptedException {
    getValuesForRelease(new ObjectEntry("RM-2539762", "250717R"));
    //getValuesForRelease(new ObjectEntry("RM-1763590", "240529I"));
  }
}
