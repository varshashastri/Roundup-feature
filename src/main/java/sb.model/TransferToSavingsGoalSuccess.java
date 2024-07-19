package sb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TransferToSavingsGoalSuccess {
    private String transferUid;
    private boolean success;
}
