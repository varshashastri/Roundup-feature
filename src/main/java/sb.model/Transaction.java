package sb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    @JsonProperty
    String feedItemUid;
    @JsonProperty
    String categoryUid;
    @JsonProperty
    Amount amount;
    @JsonProperty
    Amount sourceAmount;
    @JsonProperty
    String direction;
    @JsonProperty
    String spendingCategory;
}
