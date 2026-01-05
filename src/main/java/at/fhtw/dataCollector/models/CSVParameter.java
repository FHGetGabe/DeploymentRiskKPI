package at.fhtw.dataCollector.models;

import at.fhtw.jira.models.DefectStatValue;
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

  private String releaseNumber;
  private String releaseType;
  private Integer storyCount;
  private Integer totalDefectCount;
  @JsonIgnore
  private ReleaseStoryValues releaseStoryValues;
  @JsonIgnore
  private ReleaseXLRValues releaseXLRValues;
  @JsonIgnore
  private DefectValueSum testDefectValueSum;
  @JsonIgnore
  private DefectValueSum pilotDefectValueSum;
  @JsonIgnore
  private DefectValueSum kundenabnahmeDefectValueSum;
  @JsonIgnore
  private DefectValueSum productionDefectValueSum;
  @JsonIgnore
  private DefectStatValue defectStatValue;
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

  public int getTotalTestDefectSum() {
    return testDefectValueSum.getTotalSum();
  }

  public int getTotalTestDefectWeightedSum() {
    return testDefectValueSum.getWeightedSum();
  }

  public int getTotalM0TestDefectSum() {
    return testDefectValueSum.getTotalM0Sum();
  }

  public int getTotalM1TestDefectSum() {
    return testDefectValueSum.getTotalM1Sum();
  }

  public int getTotalM2TestDefectSum() {
    return testDefectValueSum.getTotalM2Sum();
  }

  public int getTotalM3TestDefectSum() {
    return testDefectValueSum.getTotalM3Sum();
  }

  public int getTotalPilotDefectSum() {
    return pilotDefectValueSum.getTotalSum();
  }

  public int getTotalPilotDefectWeightedSum() {
    return pilotDefectValueSum.getWeightedSum();
  }

  public int getTotalM0PilotDefectSum() {
    return pilotDefectValueSum.getTotalM0Sum();
  }

  public int getTotalM1PilotDefectSum() {
    return pilotDefectValueSum.getTotalM1Sum();
  }

  public int getTotalM2PilotDefectSum() {
    return pilotDefectValueSum.getTotalM2Sum();
  }

  public int getTotalM3PilotDefectSum() {
    return pilotDefectValueSum.getTotalM3Sum();
  }

  public int getTotalKundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum.getTotalSum();
  }

  public int getTotalKundenabnahmeDefectWeightedSum() {
    return kundenabnahmeDefectValueSum.getWeightedSum();
  }

  public int getTotalM0KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum.getTotalM0Sum();
  }

  public int getTotalM1KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum.getTotalM1Sum();
  }

  public int getTotalM2KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum.getTotalM2Sum();
  }

  public int getTotalM3KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum.getTotalM3Sum();
  }

  public int getTotalProductionDefectSum() {
    return productionDefectValueSum.getTotalSum();
  }

  public int getTotalProductionDefectWeightedSum() {
    return productionDefectValueSum.getWeightedSum();
  }

  public int getTotalM0ProductionDefectSum() {
    return productionDefectValueSum.getTotalM0Sum();
  }

  public int getTotalM1ProductionDefectSum() {
    return productionDefectValueSum.getTotalM1Sum();
  }

  public int getTotalM2ProductionDefectSum() {
    return productionDefectValueSum.getTotalM2Sum();
  }

  public int getTotalM3ProductionDefectSum() {
    return productionDefectValueSum.getTotalM3Sum();
  }

  public double getNumericCustomerAcceptanceRelevant() {
    return releaseStoryValues.getNumericCustomerAcceptanceRelevant();
  }

  public double getIssueDependencyRatio() {
    return releaseStoryValues.getIssueDependencyRatio();
  }

  public int getCriticalIssueCount() {
    return releaseStoryValues.getCriticalIssueCount();
  }

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

  public double getNumberOfOperators() {
    return releaseXLRValues.getNumberOfOperators();
  }

  public double getKnownRiskIfNotDeployed() {
    return releaseXLRValues.getKnownRiskIfNotDeployed();
  }

  public double getAverageResolutionTimeInDays() {
    return defectStatValue.getAverageResolutionTimeInDays();
  }

  public double getTransformedDaysToDeployment() {
    return defectStatValue.getTransformedDaysToDeployment();
  }

}