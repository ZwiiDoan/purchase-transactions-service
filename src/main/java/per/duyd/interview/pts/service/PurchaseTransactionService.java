package per.duyd.interview.pts.service;

import static per.duyd.interview.pts.mapper.PurchaseTransactionMapper.fromPurchaseTransactionRequest;
import static per.duyd.interview.pts.mapper.PurchaseTransactionMapper.toConvertedTransactionResponse;
import static per.duyd.interview.pts.mapper.PurchaseTransactionMapper.toPurchaseTransactionResponse;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import per.duyd.interview.pts.dto.ConvertedTransactionResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionRequest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.entity.PurchaseTransaction;
import per.duyd.interview.pts.enums.ErrorCode;
import per.duyd.interview.pts.exception.DataNotFoundException;
import per.duyd.interview.pts.repository.PurchaseTransactionRepository;

@Service
@RequiredArgsConstructor
public class PurchaseTransactionService {

  private final PurchaseTransactionRepository purchaseTransactionRepository;

  private final CachingExchangeRateService cachingExchangeRateService;

  public PurchaseTransactionResponse store(PurchaseTransactionRequest purchaseTransactionRequest) {
    PurchaseTransaction purchaseTransaction = purchaseTransactionRepository.save(
        fromPurchaseTransactionRequest(purchaseTransactionRequest)
    );

    return toPurchaseTransactionResponse(purchaseTransaction);
  }

  public ConvertedTransactionResponse retrieveInCurrency(UUID transactionId, String currency) {
    return purchaseTransactionRepository.findById(transactionId)
        .map(purchaseTransaction -> {
          ExchangeRate exchangeRate = cachingExchangeRateService.getExchangeRate(currency,
              purchaseTransaction.getTransactionDate());
          return toConvertedTransactionResponse(
              purchaseTransaction,
              exchangeRate.getExchangeRate()
          );
        }).orElseThrow(() -> new DataNotFoundException("Purchase transaction does not exist",
            ErrorCode.PURCHASE_TRANSACTION_NOT_FOUND));
  }
}
