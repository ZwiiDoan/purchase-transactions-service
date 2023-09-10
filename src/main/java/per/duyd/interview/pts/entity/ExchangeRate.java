package per.duyd.interview.pts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ExchangeRateId.class)
public class ExchangeRate {
  @Id
  private String currency;

  @Id
  private LocalDate effectiveDate;

  @NotNull
  private Double exchangeRate;
}
