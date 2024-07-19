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
public class Amount {
    @JsonProperty
    String currency;
    @JsonProperty
    int minorUnits;

    public int getMinorUnits() {
        return minorUnits;
    }

    public String getCurrency() {
        return currency;
    }
}
