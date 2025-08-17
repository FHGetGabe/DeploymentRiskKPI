package at.fhtw.jira.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private Integer startAt;
    private Integer maxResults;
    private Integer total;
}
