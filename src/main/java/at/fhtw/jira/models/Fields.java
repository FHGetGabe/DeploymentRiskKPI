package at.fhtw.jira.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
public class Fields {
  @JsonProperty ("customfield_18910")
  private CustomFieldOption customerAcceptanceRelevant;
  @JsonProperty("issuelinks")
  private List<IssueLink> issueLinks;
  @JsonProperty ("customfield_16410")
  private List<String> application;
  private String created;
  @JsonProperty ("resolutiondate")
  private String resolutionDate;
}

