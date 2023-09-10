package per.duyd.interview.pts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.duyd.interview.pts.enums.ErrorCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionErrorResponse {
  private ErrorCode errorCode;
  private String errorMessage;
}
