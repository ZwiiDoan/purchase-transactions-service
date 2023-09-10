package per.duyd.interview.pts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConvertedTransactionResponse {
  private String description;

  private LocalDate transactionDate;

  private BigDecimal purchaseAmount;

  private UUID transactionId;

  private double exchangeRate;

  private BigDecimal convertedAmount;
}
