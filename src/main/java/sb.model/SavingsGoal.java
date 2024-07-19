package sb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SavingsGoal {
    @JsonProperty
    private String savingsGoalUid;
    @JsonProperty
    private String name;
    @JsonProperty
    private String currency;
    @JsonProperty
    private TotalSaved totalSaved;
    @JsonProperty
    private String state;

}
