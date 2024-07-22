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
public class Account {
    @JsonProperty
    String accountUid;
    @JsonProperty
    String accountType;
    @JsonProperty
    String defaultCategory;
    @JsonProperty
    String currency;
    @JsonProperty
    String createdAt;
    @JsonProperty
    String name;

}
