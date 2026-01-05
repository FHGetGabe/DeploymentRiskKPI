package at.fhtw.dataCollector.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DefectValueSum {
  private final int totalSum;
  private final int weightedSum;
  private final int totalM0Sum;
  private final int totalM1Sum;
  private final int totalM2Sum;
  private final int totalM3Sum;
}
