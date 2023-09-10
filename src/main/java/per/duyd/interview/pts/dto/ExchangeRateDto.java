package per.duyd.interview.pts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateDto {
  @JsonProperty("effective_date")
  @NotNull
  private LocalDate effectiveDate;

  @JsonProperty("country_currency_desc")
  @NotNull
  private String currency;

  @JsonProperty("exchange_rate")
  private double exchangeRate;
}
