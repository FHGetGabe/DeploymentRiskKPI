package at.fhtw.digitalai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
@ToString
public class GroupedReleases {
  @JsonProperty ("live")
  private Releases liveReleases;
  @JsonProperty ("archived")
  private Releases archivedReleases;
}
