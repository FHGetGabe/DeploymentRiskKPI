package at.fhtw.dataCollector.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSVParameter {

  private Integer storyCount;
  private Integer defectCount;
  @JsonIgnore
  private ReleaseStoryValues releaseStoryValues;
  @JsonIgnore
  private ReleaseXLRValues releaseXLRValues;
  private Integer releaseCount;

  public void writeToCSV(String filePath) throws IOException {

    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema = csvMapper.schemaFor(CSVParameter.class).withHeader().withLineSeparator("\n");
    File file = new File(filePath);
    if (file.exists() && file.length() > 0) {
      schema = schema.withoutHeader();
    }

    try (FileWriter writer = new FileWriter(file, true)) {
      csvMapper.writer(schema).writeValue(writer, this);
    }

    System.out.println("Daten wurden erfolgreich in '" + filePath + "' geschrieben.");
  }
/*
  // Flache Getter für ReleaseEffortValues Felder
  public double getStoryPointsTotal() {
    return releaseStoryValues.getStoryPointsTotal();
  }

  public double getStoryPointsAverage() {
    return releaseStoryValues.getStoryPointsAverage();
  }

  public double getStoryPointsMedian() {
    return releaseStoryValues.getStoryPointsMedian();
  }

  public double getPtTotal() {
    return releaseStoryValues.getPtTotal();
  }

  public double getPtAverage() {
    return releaseStoryValues.getPtAverage();
  }

  public double getPtMedian() {
    return releaseStoryValues.getPtMedian();
  }

  public double getNumericCustomerAcceptanceRelevant() {
    return releaseStoryValues.getNumericCustomerAcceptanceRelevant();
  }
*/
  public double getSonarStatus() {
    return releaseXLRValues.getSonarStatus();
  }

  public double getModifiedImplementation() {
    return releaseXLRValues.getModifiedImplementation();
  }

  public double getModifiedConfiguration() {
    return releaseXLRValues.getModifiedConfiguration();
  }

  public double getTooLateSoftwareTransfer() {
    return releaseXLRValues.getTooLateSoftwareTransfer();
  }
  /*

  public double getNumberOfOperators() {
    return releaseXLRValues.getNumberOfOperators();
  }

  public double getKnownRiskIfNotDeployed() {
    return releaseXLRValues.getKnownRiskIfNotDeployed();
  }
 */
}