package per.duyd.interview.pts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static per.duyd.interview.pts.mapper.PurchaseTransactionMapper.roundToNearestCent;
import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import per.duyd.interview.pts.dto.ConvertedTransactionResponse;
import per.duyd.interview.pts.dto.PurchaseTransactionRequest;
import per.duyd.interview.pts.dto.PurchaseTransactionResponse;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.entity.PurchaseTransaction;
import per.duyd.interview.pts.exception.DataNotFoundException;
import per.duyd.interview.pts.repository.PurchaseTransactionRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseTransactionServiceTest {

  @Mock
  private PurchaseTransactionRepository purchaseTransactionRepository;

  @Mock
  private CachingExchangeRateService cachingExchangeRateService;

  @InjectMocks
  private PurchaseTransactionService purchaseTransactionService;

  private static final UUID TRANSACTION_ID = UUID.randomUUID();
  private static final LocalDate TRANSACTION_DATE = LocalDate.now(UTC_ZONE_ID);
  private static final String TEST_CURRENCY = "Country-Currency";
  private static final String TEST_DESCRIPTION = "Test-Description";

  @Test
  void shouldAssignIdAndStorePurchaseTransaction() {
    //Given
    PurchaseTransactionRequest purchaseTransactionRequest = PurchaseTransactionRequest.builder()
        .transactionDate(TRANSACTION_DATE)
        .purchaseAmount(BigDecimal.valueOf(12345.3467))
        .description(TEST_DESCRIPTION)
        .build();

    when(purchaseTransactionRepository.save(any(PurchaseTransaction.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    //When
    PurchaseTransactionResponse actualResponse =
        purchaseTransactionService.store(purchaseTransactionRequest);

    //Then
    assertThat(actualResponse.getTransactionId()).isInstanceOf(UUID.class);
    assertThat(actualResponse.getPurchaseAmount()).isEqualTo(BigDecimal.valueOf(12345.35));
    assertThat(actualResponse.getDescription()).isEqualTo(TEST_DESCRIPTION);
    assertThat(actualResponse.getTransactionDate()).isEqualTo(TRANSACTION_DATE);
  }

  @Test
  void shouldRetrieveConvertedTransaction() {
    //Given
    PurchaseTransaction purchaseTransaction = PurchaseTransaction.builder()
        .transactionDate(TRANSACTION_DATE)
        .purchaseAmount(BigDecimal.valueOf(12345.35))
        .description(TEST_DESCRIPTION)
        .transactionId(TRANSACTION_ID)
        .build();
    when(purchaseTransactionRepository.findById(TRANSACTION_ID))
        .thenReturn(Optional.of(purchaseTransaction));

    ExchangeRate exchangeRate = ExchangeRate.builder()
        .exchangeRate(1.2)
        .effectiveDate(TRANSACTION_DATE)
        .currency(TEST_CURRENCY)
        .build();
    when(cachingExchangeRateService.getExchangeRate(TEST_CURRENCY,
        purchaseTransaction.getTransactionDate())).thenReturn(exchangeRate);

    ConvertedTransactionResponse expectedResponse = ConvertedTransactionResponse.builder()
        .transactionDate(TRANSACTION_DATE)
        .purchaseAmount(BigDecimal.valueOf(12345.35))
        .description(TEST_DESCRIPTION)
        .convertedAmount(
            roundToNearestCent(BigDecimal.valueOf(12345.35).multiply(BigDecimal.valueOf(1.2))))
        .exchangeRate(1.2)
        .transactionId(TRANSACTION_ID)
        .build();

    //When
    ConvertedTransactionResponse actualResponse =
        purchaseTransactionService.retrieveInCurrency(TRANSACTION_ID, TEST_CURRENCY);

    //Then
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void shouldThrowDataNotFoundExceptionWhenTransactionDoesNotExist() {
    //Given
    when(purchaseTransactionRepository.findById(TRANSACTION_ID)).thenReturn(Optional.empty());

    //When & Then
    assertThrows(DataNotFoundException.class,
        () -> purchaseTransactionService.retrieveInCurrency(TRANSACTION_ID, TEST_CURRENCY));
  }
}