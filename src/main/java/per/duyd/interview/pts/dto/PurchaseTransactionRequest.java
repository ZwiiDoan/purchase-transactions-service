package per.duyd.interview.pts.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionRequest {
  @Size(max = 50)
  private String description;

  @NotNull
  @PastOrPresent
  private LocalDate transactionDate;

  @NotNull
  @Positive
  private BigDecimal purchaseAmount;
}
