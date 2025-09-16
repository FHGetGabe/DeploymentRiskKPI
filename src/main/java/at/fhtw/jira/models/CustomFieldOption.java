package at.fhtw.jira.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldOption {
  @JsonProperty("self")
  private String self;

  @JsonProperty("value")
  private String value;

  @JsonProperty("id")
  private String id;

  @JsonProperty("disabled")
  private boolean disabled;
}