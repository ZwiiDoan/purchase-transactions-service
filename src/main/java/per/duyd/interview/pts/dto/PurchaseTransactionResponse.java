package per.duyd.interview.pts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionResponse {
  private String description;

  private LocalDate transactionDate;

  private BigDecimal purchaseAmount;

  private UUID transactionId;
}
