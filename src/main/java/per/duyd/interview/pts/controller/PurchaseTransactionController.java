package per.duyd.interview.pts.controller;

import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import per.duyd.interview.pts.dto.ConvertedTransactionResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionRequest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;
import per.duyd.interview.pts.service.PurchaseTransactionService;

@RestController
@RequiredArgsConstructor
public class PurchaseTransactionController {

  private final PurchaseTransactionService purchaseTransactionService;

  @PostMapping("/v1/purchase-transaction")
  @Observed(name = "Request",
      contextualName = "Store_Purchase_Transaction",
      lowCardinalityKeyValues = {"endpoint", "/purchase-transaction"})
  public PurchaseTransactionResponse storePurchaseTransaction(
      @Valid @RequestBody PurchaseTransactionRequest purchaseTransactionRequest) {
    return purchaseTransactionService.store(purchaseTransactionRequest);
  }

  @GetMapping("/v1/purchase-transaction/{transactionId}")
  @Observed(name = "Request",
      contextualName = "Retrieve_Purchase_Transaction",
      lowCardinalityKeyValues = {"endpoint", "/purchase-transaction/{transactionId}"})
  public ConvertedTransactionResponse retrievePurchaseTransaction(
      @PathVariable UUID transactionId, @PathParam("country") @NotNull String currency) {
    return purchaseTransactionService.retrieveInCurrency(transactionId, currency);
  }
}
