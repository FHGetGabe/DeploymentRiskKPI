package at.fhtw.jira.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties (ignoreUnknown = true)
@ToString
public class Attribute {
  private Integer objectTypeAttributeId;
  private List<ObjectAttributeValue> objectAttributeValues;
}
