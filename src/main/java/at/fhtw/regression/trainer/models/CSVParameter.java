package at.fhtw.regression.trainer.models;

import at.fhtw.jira.models.ReleaseEffortValues;
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

  public static CSVParameter from (ReleaseEffortValues value,
                                   Integer storyCount,
                                   Integer defectCount) {
    return CSVParameter.builder()
                       .storyCount(storyCount)
                       .defectCount(defectCount)
                       .storyPointsTotal(value.getStoryPointsTotal())
                       .storyPointsAverage(value.getStoryPointsAverage())
                       .storyPointsMedian(value.getStoryPointsMedian())
                       .storyPoints25Percentile(value.getStoryPoints25Percentile())
                       .storyPoints35Percentile(value.getStoryPoints35Percentile())
                       .storyPoints45Percentile(value.getStoryPoints45Percentile())
                       .storyPoints55Percentile(value.getStoryPoints55Percentile())
                       .storyPoints65Percentile(value.getStoryPoints65Percentile())
                       .storyPoints75Percentile(value.getStoryPoints75Percentile())
                       .ptTotal(value.getPtTotal())
                       .ptAverage(value.getPtAverage())
                       .ptMedian(value.getPtMedian())
                       .pt25Percentile(value.getPt25Percentile())
                       .pt35Percentile(value.getPt35Percentile())
                       .pt45Percentile(value.getPt45Percentile())
                       .pt55Percentile(value.getPt55Percentile())
                       .pt65Percentile(value.getPt65Percentile())
                       .pt75Percentile(value.getPt75Percentile())
                       .build();
  }
}