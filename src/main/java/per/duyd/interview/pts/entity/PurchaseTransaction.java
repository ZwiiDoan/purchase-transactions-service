package per.duyd.interview.pts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransaction {
  @Size(max = 50)
  private String description;

  @NotNull
  @PastOrPresent
  private LocalDate transactionDate;

  @NotNull
  @Positive
  private BigDecimal purchaseAmount;

  @Id
  private UUID transactionId;
}
