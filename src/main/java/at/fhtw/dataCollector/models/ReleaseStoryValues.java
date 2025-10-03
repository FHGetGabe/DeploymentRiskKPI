package at.fhtw.dataCollector.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ReleaseStoryValues {
  private double issueDependencyRatio;
  private int criticalIssueCount;
  private double numericCustomerAcceptanceRelevant;
}
