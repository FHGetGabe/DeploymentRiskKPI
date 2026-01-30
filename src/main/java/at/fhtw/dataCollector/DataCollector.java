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
    AtomicInteger i = new AtomicInteger();
    i.set(0);
    allReleases.forEach((objectEntries, jiraReleaseType) -> {
      objectEntries.forEach(objectEntry -> {
        try {
          if(objectEntry.getLabel().equals("250403P")) {
            i.compareAndSet(0, 1);
          }
          if(i.get() == 1) {
            System.out.println("Processing release " + index.getAndIncrement() + ": " + objectEntry.getLabel());

            LocalDate startProductionDeploymentDate = getStartProductionDeploymentDate(objectEntry,
                                                                                       jiraReleaseType);
            getValuesForRelease(objectEntry, jiraReleaseType, startProductionDeploymentDate);
          }

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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String dateString = deploymentDate.format(formatter);


    String createdQueryProd = "created > " + dateString;
    String createdQuery = "created < " + dateString;
    DefectValueSum testDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                   DefectValues.Test, createdQuery);
    DefectValueSum pilotDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                    DefectValues.Pilot, createdQuery);
    DefectValueSum kundenabnahmeDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                            DefectValues.Kundenabnahme, createdQuery);
    DefectValueSum productionDefectValueSum = Jira.getFoundInDefectCount(objectEntry.getObjectKey(),
                                                                         DefectValues.Produktion, createdQueryProd);

    DefectStatValue defectStatValueKundenabnahme = Jira.getDefectStatValuesWithKundenabnahme(
        objectEntry.getObjectKey(),
        deploymentDate);

    DefectStatValue defectStatValueTest = Jira.getDefectStatValuesWithOutKundenabnahme(objectEntry.getObjectKey(),
                                                                                       deploymentDate);
    System.out.println(defectStatValueKundenabnahme);
    System.out.println(defectStatValueTest);

    Integer releaseCountDigitalAi = Digitalai.getReleaseCount(objectEntry.getLabel(),
                                                              HttpHelper.Context.DIGITAL_AI);
    Integer releaseCountDigitalAiArchive = Digitalai.getReleaseCount(objectEntry.getLabel(),
                                                                     HttpHelper.Context.DIGITAL_AI_ARCHIVE);

    ReleaseStoryValues releaseStoryValues = Jira.getReleaseStoryValues(objectEntry.getObjectKey());

    Integer totalReleaseCount = releaseCountDigitalAi + releaseCountDigitalAiArchive;
    System.out.println("Total Release Count: " + totalReleaseCount);

    if (totalReleaseCount == 0 || storyCount == 0) {
      System.out.println("Skip Release: " + objectEntry.getLabel());
      return;
    }

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
                                            .defectStatValueKundenabnahme(
                                                defectStatValueKundenabnahme)
                                            .defectStatValueTest(defectStatValueTest)
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
}
