package at.fhtw.dataCollector.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseXLRValues {

  private double sonarStatus;
  private double modifiedImplementation;
  private double modifiedConfiguration;
  private double numberOfOperators;
  private double tooLateSoftwareTransfer;
  private double knownRiskIfNotDeployed;

  public ReleaseXLRValues plus (ReleaseXLRValues other) {

    return ReleaseXLRValues.builder()
                           .sonarStatus(getMean(this.sonarStatus, other.sonarStatus))
                           .modifiedImplementation(getMean(this.modifiedImplementation,
                                                           other.modifiedImplementation))
                           .modifiedConfiguration(getMean(this.modifiedConfiguration,
                                                          other.modifiedConfiguration))
                           .numberOfOperators(getMean(this.numberOfOperators,
                                                      other.numberOfOperators))
                           .tooLateSoftwareTransfer(getMean(this.tooLateSoftwareTransfer,
                                                            other.tooLateSoftwareTransfer))
                           .knownRiskIfNotDeployed(getMean(this.knownRiskIfNotDeployed,
                                                           other.knownRiskIfNotDeployed))
                           .build();
  }

  private double getMean (double value1,
                          double value2) {
    return (value1 + value2) / 2;
  }

}
