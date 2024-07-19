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
public class CreateSavingsGoalSuccess {
    @JsonProperty
    private String savingsGoalUid;

    @JsonProperty
    private boolean success;
}
