package per.duyd.interview.pts.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;
import per.duyd.interview.pts.dto.ConvertedTransactionResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionRequest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;
import per.duyd.interview.pts.entity.PurchaseTransaction;

public class PurchaseTransactionMapper {
  private PurchaseTransactionMapper() {
    //For Jacoco Coverage
  }

  public static PurchaseTransaction fromPurchaseTransactionRequest(
      PurchaseTransactionRequest purchaseTransactionRequest) {
    return PurchaseTransaction.builder()
        .transactionId(UUID.randomUUID())
        .transactionDate(purchaseTransactionRequest.getTransactionDate())
        .description(purchaseTransactionRequest.getDescription())
        .purchaseAmount(roundToNearestCent(purchaseTransactionRequest.getPurchaseAmount()))
        .build();
  }

  public static PurchaseTransactionResponse toPurchaseTransactionResponse(
      PurchaseTransaction purchaseTransaction) {
    return PurchaseTransactionResponse.builder()
        .transactionId(purchaseTransaction.getTransactionId())
        .transactionDate(purchaseTransaction.getTransactionDate())
        .description(purchaseTransaction.getDescription())
        .purchaseAmount(purchaseTransaction.getPurchaseAmount())
        .build();
  }

  public static BigDecimal roundToNearestCent(BigDecimal amount) {
    return Optional.ofNullable(amount).map(it -> it.setScale(2, RoundingMode.HALF_EVEN))
        .orElse(null);
  }

  public static ConvertedTransactionResponse toConvertedTransactionResponse(
      PurchaseTransaction purchaseTransaction, double exchangeRate) {
    return ConvertedTransactionResponse.builder()
        .transactionDate(purchaseTransaction.getTransactionDate())
        .purchaseAmount(purchaseTransaction.getPurchaseAmount())
        .transactionId(purchaseTransaction.getTransactionId())
        .description(purchaseTransaction.getDescription())
        .convertedAmount(roundToNearestCent(
            purchaseTransaction.getPurchaseAmount().multiply(BigDecimal.valueOf(exchangeRate))
        ))
        .exchangeRate(exchangeRate)
        .build();
  }
}
