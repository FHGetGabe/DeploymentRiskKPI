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

  private double storyPointsTotal;
  private double storyPointsAverage;
  private double storyPointsMedian;

  private double ptTotal;
  private double ptAverage;
  private double ptMedian;

  private double numericCustomerAcceptanceRelevant;
}
