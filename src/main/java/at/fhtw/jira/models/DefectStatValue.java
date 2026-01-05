package at.fhtw.jira.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
@ToString
@Builder
public class DefectStatValue {
  private double averageResolutionTimeInDays;
  private double transformedDaysToDeployment;
}
