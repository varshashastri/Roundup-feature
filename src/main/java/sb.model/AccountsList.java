package sb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountsList {
    @JsonProperty
    List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

}
