package at.fhtw.dataCollector;

import at.fhtw.dataCollector.models.CSVParameter;
import at.fhtw.dataCollector.models.DefectValueSum;
import at.fhtw.dataCollector.models.ReleaseStoryValues;
import at.fhtw.dataCollector.models.ReleaseXLRValues;
import at.fhtw.digitalai.Digitalai;
import at.fhtw.http.HttpHelper;
import at.fhtw.jira.Jira;
import at.fhtw.jira.models.DefectStatValue;
import at.fhtw.jira.models.DefectValues;
import at.fhtw.jira.models.ObjectAttributeValue;
import at.fhtw.jira.models.ObjectEntry;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCollector {

  public static void main (String[] args) throws IOException, InterruptedException {

    List<ObjectEntry> hauptReleasesObjectEntries = Jira.getHauptReleasesObjectEntries();
    List<ObjectEntry> sonderReleasesObjectEntries = Jira.getSonderReleasesObjectEntries();
    List<ObjectEntry> hotfixReleasesObjectEntries = Jira.getHotfixReleasesObjectEntries();

    Map<List<ObjectEntry>, String> allReleases = new HashMap<>();
    allReleases.put(hauptReleasesObjectEntries, "Hauptreleases");
    allReleases.put(sonderReleasesObjectEntries, "Sonderreleases");
    allReleases.put(hotfixReleasesObjectEntries, "Hotfixreleases");

    AtomicInteger index = new AtomicInteger(1);
    System.out.println("Found " + hauptReleasesObjectEntries.size() + " Hauptreleases.");
    System.out.println("Found " + sonderReleasesObjectEntries.size() + " Sonderreleases.");
    System.out.println("Found " + hotfixReleasesObjectEntries.size() + " Hotfixreleases.");
    System.out.println(hauptReleasesObjectEntries);
    allReleases.forEach((objectEntries, jiraReleaseType) -> {
      objectEntries.forEach(objectEntry -> {
        try {
          System.out.println("Processing release " + index.getAndIncrement() + ": " + objectEntry.getLabel());
          LocalDate startProductionDeploymentDate = getStartProductionDeploymentDate(objectEntry,
                                                                                     jiraReleaseType);
          getValuesForRelease(objectEntry, jiraReleaseType, startProductionDeploymentDate);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    });

    //csvParameters.writeToCSV("src/main/resources/stories.csv");

  }

  private static void getValuesForRelease (ObjectEntry objectEntry,
                                           String jiraReleaseType,
                                           LocalDate deploymentDate) throws IOException, InterruptedException {

    Integer storyCount = Jira.getStoryCount(objectEntry.getObjectKey());
    Integer totalDefectCount = Jira.getTotalDefectCount(objectEntry.getObjectKey());
    DefectValueSum testDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                   DefectValues.Test);
    DefectValueSum pilotDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                    DefectValues.Pilot);
    DefectValueSum kundenabnahmeDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                            DefectValues.Kundenabnahme);
    DefectValueSum productionDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                         DefectValues.Produktion);

    DefectStatValue defectStatValue = Jira.getDefectStatValues(objectEntry.getObjectKey(),
                                                               deploymentDate);

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

    if (totalReleaseCount == 0 || storyCount == 0) {
      System.out.println("Skip Release: " + objectEntry.getLabel());
      return;
    }

    CSVParameter csvParameter = CSVParameter.builder()
                                            .releaseStoryValues(releaseStoryValues)
                                            .releaseXLRValues(totalReleaseXLRValues)
                                            .releaseCount(totalReleaseCount)
                                            .releaseNumber(objectEntry.getLabel())
                                            .releaseType(getActualReleaseType(objectEntry.getLabel()))
                                            .storyCount(storyCount)
                                            .totalDefectCount(totalDefectCount)
                                            .testDefectValueSum(testDefectValueSum)
                                            .pilotDefectValueSum(pilotDefectValueSum)
                                            .kundenabnahmeDefectValueSum(kundenabnahmeDefectValueSum)
                                            .productionDefectValueSum(productionDefectValueSum)
                                            .defectStatValue(defectStatValue)
                                            .build();
    csvParameter.writeToCSV("src/main/resources/stories.csv");
  }

  private static String getStartProductionDeployment (ObjectEntry objectEntry,
                                                      String releaseType) {
    Integer attributeId = getAttributeId(releaseType);
    return objectEntry.getAttributes().stream()
                      .filter(attr -> attr.getObjectTypeAttributeId().equals(attributeId))
                      .findFirst()
                      .flatMap(attr -> attr.getObjectAttributeValues().stream().findFirst())
                      .map(ObjectAttributeValue::getValue)
                      .orElse(null);

  }

  private static LocalDate getStartProductionDeploymentDate (ObjectEntry objectEntry,
                                                             String releaseType) {
    String startKundenabnahme = getStartProductionDeployment(objectEntry, releaseType);
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      return LocalDate.parse(startKundenabnahme, formatter);
    } catch (DateTimeParseException ex) {
      System.err.println("Unable to parse date: " + startKundenabnahme);
      return null;
    }
  }


  private static Integer getAttributeId (String releaseType) {
    return switch (releaseType) {
      case "Hauptreleases" -> 851;
      case "Sonderreleases" -> 867;
      case "Hotfixreleases" -> 877;
      default -> throw new IllegalArgumentException("Unknown release type: " + releaseType);
    };
  }

  private static String getActualReleaseType (String releaseNumber) {
    char lastChar = releaseNumber.charAt(releaseNumber.length() - 1);

    return switch (lastChar) {
      case 'R' -> "Hauptrelease";
      case 'P' -> "Produktrelease";
      case 'U' -> "Ultimorelease";
      case 'S' -> "Sonderrelease";
      case 'H' -> "Hotfixrelease";
      case 'T' -> "Technischesrelease";
      case 'G' -> "Geosrelease";
      case 'M' -> "RBI Minor Release";
      default -> throw new IllegalArgumentException("Unknown release type lastChar: " + lastChar);
    };
  }


  public static void main1 (String[] args) throws IOException, InterruptedException {
    //getValuesForRelease(new ObjectEntry("RM-2539762", "240507S"), "Sonder");
  }
}
