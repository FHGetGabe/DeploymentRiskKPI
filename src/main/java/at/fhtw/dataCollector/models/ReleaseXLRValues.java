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

  public ReleaseXLRValues plus (ReleaseXLRValues other,
                                Integer releaseCountDigitalAi,
                                Integer releaseCountDigitalAiArchive) {

    return ReleaseXLRValues.builder()
                           .sonarStatus(getWeightedMean(this.sonarStatus,
                                                        other.sonarStatus,
                                                        releaseCountDigitalAi,
                                                        releaseCountDigitalAiArchive))
                           .modifiedImplementation(getWeightedMean(this.modifiedImplementation,
                                                                   other.modifiedImplementation,
                                                                   releaseCountDigitalAi,
                                                                   releaseCountDigitalAiArchive))
                           .modifiedConfiguration(getWeightedMean(this.modifiedConfiguration,
                                                                  other.modifiedConfiguration,
                                                                  releaseCountDigitalAi,
                                                                  releaseCountDigitalAiArchive))
                           .numberOfOperators(getWeightedMean(this.numberOfOperators,
                                                              other.numberOfOperators,
                                                              releaseCountDigitalAi,
                                                              releaseCountDigitalAiArchive))
                           .tooLateSoftwareTransfer(getWeightedMean(this.tooLateSoftwareTransfer,
                                                                    other.tooLateSoftwareTransfer,
                                                                    releaseCountDigitalAi,
                                                                    releaseCountDigitalAiArchive))
                           .knownRiskIfNotDeployed(getWeightedMean(this.knownRiskIfNotDeployed,
                                                                   other.knownRiskIfNotDeployed,
                                                                   releaseCountDigitalAi,
                                                                   releaseCountDigitalAiArchive))
                           .build();
  }

  private double getWeightedMean (double value1,
                                  double value2,
                                  int weight1,
                                  int weight2) {
    int totalWeight = weight1 + weight2;
    return ((value1 * weight1) + (value2 * weight2)) / totalWeight;
  }

  public static ReleaseXLRValues getTotalReleaseXLRValues (ReleaseXLRValues releaseXLRValues,
                                                           ReleaseXLRValues releaseXLRArchiveValues,
                                                           Integer releaseCountDigitalAi,
                                                           Integer releaseCountDigitalAiArchive) {
    if (releaseXLRValues == null) {
      return releaseXLRArchiveValues;
    }
    if (releaseXLRArchiveValues == null) {
      return releaseXLRValues;
    }
    return releaseXLRValues.plus(releaseXLRArchiveValues,
                                 releaseCountDigitalAi,
                                 releaseCountDigitalAiArchive);
  }
}
