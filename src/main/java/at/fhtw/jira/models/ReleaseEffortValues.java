package at.fhtw.jira.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ReleaseEffortValues {

  private Double storyPointsTotal;
  private Double storyPointsAverage;
  private Double storyPointsMedian;
  private Double storyPoints25Percentile;
  private Double storyPoints35Percentile;
  private Double storyPoints45Percentile;
  private Double storyPoints55Percentile;
  private Double storyPoints65Percentile;
  private Double storyPoints75Percentile;

  private Double ptTotal;
  private Double ptAverage;
  private Double ptMedian;
  private Double pt25Percentile;
  private Double pt35Percentile;
  private Double pt45Percentile;
  private Double pt55Percentile;
  private Double pt65Percentile;
  private Double pt75Percentile;
}
