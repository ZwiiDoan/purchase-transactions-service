package per.duyd.interview.pts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.duyd.interview.pts.validation.TransactionDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransaction {
  @Size(max = 50)
  private String description;

  @NotNull
  @TransactionDate
  private LocalDate transactionDate;

  @NotNull
  private BigDecimal purchaseAmount;

  @Id
  private UUID transactionId;
}
