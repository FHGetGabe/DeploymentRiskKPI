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
  private DefectStatValue defectStatValueKundenabnahme;
  @JsonIgnore
  private DefectStatValue defectStatValueTest;
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



  public Integer getTotalTestDefectSum() {
    return testDefectValueSum != null ? testDefectValueSum.getTotalSum() : null;
  }

  public Integer getTotalTestDefectWeightedSum() {
    return testDefectValueSum != null ? testDefectValueSum.getWeightedSum() : null;
  }

  public Integer getTotalM0TestDefectSum() {
    return testDefectValueSum != null ? testDefectValueSum.getTotalM0Sum() : null;
  }

  public Integer getTotalM1TestDefectSum() {
    return testDefectValueSum != null ? testDefectValueSum.getTotalM1Sum() : null;
  }

  public Integer getTotalM2TestDefectSum() {
    return testDefectValueSum != null ? testDefectValueSum.getTotalM2Sum() : null;
  }

  public Integer getTotalM3TestDefectSum() {
    return testDefectValueSum != null ? testDefectValueSum.getTotalM3Sum() : null;
  }

  public Integer getTotalPilotDefectSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getTotalSum() : null;
  }

  public Integer getTotalPilotDefectWeightedSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getWeightedSum() : null;
  }

  public Integer getTotalM0PilotDefectSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getTotalM0Sum() : null;
  }

  public Integer getTotalM1PilotDefectSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getTotalM1Sum() : null;
  }

  public Integer getTotalM2PilotDefectSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getTotalM2Sum() : null;
  }

  public Integer getTotalM3PilotDefectSum() {
    return pilotDefectValueSum != null ? pilotDefectValueSum.getTotalM3Sum() : null;
  }

  public Integer getTotalKundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getTotalSum() : null;
  }

  public Integer getTotalKundenabnahmeDefectWeightedSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getWeightedSum() : null;
  }

  public Integer getTotalM0KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getTotalM0Sum() : null;
  }

  public Integer getTotalM1KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getTotalM1Sum() : null;
  }

  public Integer getTotalM2KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getTotalM2Sum() : null;
  }

  public Integer getTotalM3KundenabnahmeDefectSum() {
    return kundenabnahmeDefectValueSum != null ? kundenabnahmeDefectValueSum.getTotalM3Sum() : null;
  }

  public Integer getTotalProductionDefectSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getTotalSum() : null;
  }

  public Integer getTotalProductionDefectWeightedSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getWeightedSum() : null;
  }

  public Integer getTotalM0ProductionDefectSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getTotalM0Sum() : null;
  }

  public Integer getTotalM1ProductionDefectSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getTotalM1Sum() : null;
  }

  public Integer getTotalM2ProductionDefectSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getTotalM2Sum() : null;
  }

  public Integer getTotalM3ProductionDefectSum() {
    return productionDefectValueSum != null ? productionDefectValueSum.getTotalM3Sum() : null;
  }

  public Double getNumericCustomerAcceptanceRelevant() {
    return releaseStoryValues != null ? releaseStoryValues.getNumericCustomerAcceptanceRelevant() : null;
  }

  public Double getIssueDependencyRatio() {
    return releaseStoryValues != null ? releaseStoryValues.getIssueDependencyRatio() : null;
  }

  public Integer getCriticalIssueCount() {
    return releaseStoryValues != null ? releaseStoryValues.getCriticalIssueCount() : null;
  }

  public Double getSonarStatus() {
    return releaseXLRValues != null ? releaseXLRValues.getSonarStatus() : null;
  }

  public Double getModifiedImplementation() {
    return releaseXLRValues != null ? releaseXLRValues.getModifiedImplementation() : null;
  }

  public Double getModifiedConfiguration() {
    return releaseXLRValues != null ? releaseXLRValues.getModifiedConfiguration() : null;
  }

  public Double getTooLateSoftwareTransfer() {
    return releaseXLRValues != null ? releaseXLRValues.getTooLateSoftwareTransfer() : null;
  }

  public Double getNumberOfOperators() {
    return releaseXLRValues != null ? releaseXLRValues.getNumberOfOperators() : null;
  }

  public Double getKnownRiskIfNotDeployed() {
    return releaseXLRValues != null ? releaseXLRValues.getKnownRiskIfNotDeployed() : null;
  }

  public Double getAverageResolutionTimeInDaysKundenabnahme() {
    return defectStatValueKundenabnahme != null ? defectStatValueKundenabnahme.getAverageResolutionTimeInDays() : null;
  }

  public Double getTransformedDaysToDeploymentKundenabnahme() {
    return defectStatValueKundenabnahme != null ? defectStatValueKundenabnahme.getTransformedDaysToDeployment() : null;
  }

  public Double getAverageResolutionTimeInDaysTest() {
    return defectStatValueTest != null ? defectStatValueTest.getAverageResolutionTimeInDays() : null;
  }

  public Double getTransformedDaysToDeploymentTest() {
    return defectStatValueTest != null ? defectStatValueTest.getTransformedDaysToDeployment() : null;
  }

  public Integer getTotalCustomerAcceptanceRelevant() {
    return releaseStoryValues != null ? releaseStoryValues.getTotalCustomerAcceptanceRelevant() : null;
  }

}