package at.fhtw.digitalai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
public class ReleaseCountResponse {
  private StatusData live;
  private StatusData archived;
  private StatusData all;

}
