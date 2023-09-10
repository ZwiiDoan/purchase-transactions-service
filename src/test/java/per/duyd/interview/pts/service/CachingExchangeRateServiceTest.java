package per.duyd.interview.pts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import per.duyd.interview.pts.client.TreasuryClient;
import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.repository.ExchangeRateRepository;

@ExtendWith(MockitoExtension.class)
class CachingExchangeRateServiceTest {

  @Mock
  private TreasuryClient treasuryClient;

  @Mock
  private ExchangeRateRepository exchangeRateRepository;

  @InjectMocks
  private CachingExchangeRateService cachingExchangeRateService;

  private static final String TEST_CURRENCY = "Country-Currency";

  @Test
  void shouldGetApplicableExchangeRateFromDatabase() {
    //Given
    LocalDate now = LocalDate.now();
    LocalDate transactionDate = now.minusMonths(1);
    ExchangeRate expectedExchangeRate =
        ExchangeRate.builder().effectiveDate(now.minusMonths(2)).build();
    when(exchangeRateRepository.findByCurrencyAndEffectiveDateBetween(TEST_CURRENCY,
        transactionDate.minusMonths(6), transactionDate))
        .thenReturn(List.of(expectedExchangeRate));

    //When & Then
    assertThat(cachingExchangeRateService.getExchangeRate(TEST_CURRENCY, transactionDate))
        .isEqualTo(expectedExchangeRate);
  }

  @Test
  void shouldGetApplicableExchangeRateFromApiWhenItDoesNotExistInDatabase() {
    //When
    LocalDate now = LocalDate.now();
    LocalDate recordDate = now.minusMonths(2);
    LocalDate transactionDate = now.minusMonths(1);

    when(exchangeRateRepository.findByCurrencyAndEffectiveDateBetween(TEST_CURRENCY,
        transactionDate.minusMonths(6), transactionDate)).thenReturn(List.of());
    when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
        .exchangeRate(1.2)
        .currency(TEST_CURRENCY)
        .effectiveDate(recordDate)
        .build();
    when(treasuryClient.getLatestExchangeRate(TEST_CURRENCY, transactionDate)).thenReturn(
        exchangeRateDto);

    //When
    ExchangeRate actualExchangeRate = cachingExchangeRateService.getExchangeRate(TEST_CURRENCY,
        transactionDate);

    //Then
    assertThat(actualExchangeRate.getExchangeRate()).isEqualTo(exchangeRateDto.getExchangeRate());
    assertThat(actualExchangeRate.getCurrency()).isEqualTo(exchangeRateDto.getCurrency());
    assertThat(actualExchangeRate.getEffectiveDate()).isEqualTo(exchangeRateDto.getEffectiveDate());
  }
}