package per.duyd.interview.pts.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.duyd.interview.pts.validation.TransactionDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionRequest {
  @Size(max = 50)
  private String description;

  @NotNull
  @TransactionDate
  private LocalDate transactionDate;

  @NotNull
  private BigDecimal purchaseAmount;
}
