package per.duyd.interview.pts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static per.duyd.interview.pts.util.DateTimeUtil.getSecondsBetween;
import static per.duyd.interview.pts.util.DateTimeUtil.toUtcDateTime;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import per.duyd.interview.pts.client.TreasuryClient;
import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.exception.DataNotFoundException;
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
  void shouldGetApplicableExchangeRateFromCache() {
    //Given
    LocalDate now = LocalDate.now();
    LocalDate transactionDate = now.minusMonths(1);
    ExchangeRate expectedExchangeRate =
        ExchangeRate.builder().recordDate(now.minusMonths(2)).build();
    when(exchangeRateRepository.findById(TEST_CURRENCY)).thenReturn(
        Optional.of(expectedExchangeRate));

    //When & Then
    assertThat(cachingExchangeRateService.getExchangeRate(TEST_CURRENCY, transactionDate))
        .isEqualTo(expectedExchangeRate);
  }

  @Test
  void shouldGetApplicableExchangeRateFromApiWhenItDoesNotExistInCache() {
    //When
    LocalDate now = LocalDate.now();
    LocalDate recordDate = now.minusMonths(2);
    LocalDate transactionDate = now.minusMonths(1);

    when(exchangeRateRepository.findById(TEST_CURRENCY)).thenReturn(Optional.empty());
    when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
        .exchangeRate(1.2)
        .currency(TEST_CURRENCY)
        .recordDate(recordDate)
        .build();
    when(treasuryClient.getLatestExchangeRate(TEST_CURRENCY)).thenReturn(exchangeRateDto);

    //When
    ExchangeRate actualExchangeRate = cachingExchangeRateService.getExchangeRate(TEST_CURRENCY,
        transactionDate);

    //Then
    assertThat(actualExchangeRate.getExchangeRate()).isEqualTo(exchangeRateDto.getExchangeRate());
    assertThat(actualExchangeRate.getCurrency()).isEqualTo(exchangeRateDto.getCurrency());
    assertThat(actualExchangeRate.getRecordDate()).isEqualTo(exchangeRateDto.getRecordDate());
    assertThat(actualExchangeRate.getExpirationInSeconds()).isLessThanOrEqualTo(
        getSecondsBetween(toUtcDateTime(now), toUtcDateTime(now.plusMonths(4)))
    );
  }

  @Test
  void shouldGetApplicableExchangeRateFromApiWhenCachedExchangeRateIsNotApplicable() {
    //When
    LocalDate now = LocalDate.now();
    LocalDate recordDate = now.minusMonths(3);
    LocalDate transactionDate = now.minusMonths(2);
    LocalDate cachedDate = now.minusMonths(1);

    ExchangeRateDto exchangeRateDto = setupTestExchangeRates(recordDate, cachedDate);

    //When
    ExchangeRate actualExchangeRate = cachingExchangeRateService.getExchangeRate(TEST_CURRENCY,
        transactionDate);

    //Then
    assertThat(actualExchangeRate.getExchangeRate()).isEqualTo(exchangeRateDto.getExchangeRate());
    assertThat(actualExchangeRate.getCurrency()).isEqualTo(exchangeRateDto.getCurrency());
    assertThat(actualExchangeRate.getRecordDate()).isEqualTo(exchangeRateDto.getRecordDate());
    assertThat(actualExchangeRate.getExpirationInSeconds()).isLessThanOrEqualTo(
        getSecondsBetween(toUtcDateTime(now), toUtcDateTime(now.plusMonths(3)))
    );
  }

  @Test
  void shouldThrowDataNotFoundExceptionWhenNoExchangeRateApplicable() {
    //When
    LocalDate now = LocalDate.now();
    LocalDate recordDate = now.minusMonths(1);
    LocalDate transactionDate = now.minusMonths(3);
    LocalDate cachedDate = now.minusMonths(2);

    setupTestExchangeRates(recordDate, cachedDate);

    //When
    assertThrows(DataNotFoundException.class,
        () -> cachingExchangeRateService.getExchangeRate(TEST_CURRENCY, transactionDate));
  }

  private ExchangeRateDto setupTestExchangeRates(LocalDate recordDate, LocalDate cachedDate) {
    ExchangeRate cachedExchangeRate = ExchangeRate.builder()
        .recordDate(cachedDate)
        .exchangeRate(1.2)
        .currency(TEST_CURRENCY)
        .build();
    when(exchangeRateRepository.findById(TEST_CURRENCY)).thenReturn(
        Optional.of(cachedExchangeRate));
    when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
        .exchangeRate(1.2)
        .currency(TEST_CURRENCY)
        .recordDate(recordDate)
        .build();
    when(treasuryClient.getLatestExchangeRate(TEST_CURRENCY)).thenReturn(exchangeRateDto);
    return exchangeRateDto;
  }

  @Test
  void shouldNotCacheExchangeRateWithNegativeExpirationInSeconds() {
    //Given
    ExchangeRateDto exchangeRateDto =
        ExchangeRateDto.builder().recordDate(LocalDate.now().minusMonths(7)).build();

    //When & Then
    assertThat(cachingExchangeRateService.getExpirationInSeconds(exchangeRateDto)).isEqualTo(0);
  }
}