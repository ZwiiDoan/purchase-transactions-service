package per.duyd.interview.pts.service;

import static per.duyd.interview.pts.exception.DataNotFoundException.EXCHANGE_RATE_NOT_FOUND_EXCEPTION;
import static per.duyd.interview.pts.mapper.ExchangeRateMapper.fromExchangeRateDto;
import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;
import static per.duyd.interview.pts.util.DateTimeUtil.getSecondsBetween;
import static per.duyd.interview.pts.util.DateTimeUtil.toUtcDateTime;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import per.duyd.interview.pts.client.TreasuryClient;
import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.entity.ExchangeRate;
import per.duyd.interview.pts.repository.ExchangeRateRepository;

@Service
@RequiredArgsConstructor
public class CachingExchangeRateService {
  private final TreasuryClient treasuryClient;

  private final ExchangeRateRepository exchangeRateRepository;

  @Value("${application.exchangeRate.validMonths}")
  private int validMonths = 6;

  public ExchangeRate getExchangeRate(@NotNull String currency,
                                      @NotNull LocalDate transactionDate) {
    return exchangeRateRepository.findById(currency)
        .filter(exchangeRate -> isApplicableToTransactionDate(transactionDate, exchangeRate))
        .orElseGet(() -> getAndCacheExchangeRateFromApi(currency, transactionDate));
  }

  private boolean isApplicableToTransactionDate(LocalDate transactionDate,
                                                ExchangeRate exchangeRate) {
    return !exchangeRate.getRecordDate().isAfter(transactionDate);
  }

  private ExchangeRate getAndCacheExchangeRateFromApi(String currency, LocalDate transactionDate) {
    ExchangeRateDto exchangeRateDto = treasuryClient.getLatestExchangeRate(currency);
    ExchangeRate cachedExchangeRate = cacheExchangeRate(exchangeRateDto);

    if (isApplicableToTransactionDate(transactionDate, cachedExchangeRate)) {
      return cachedExchangeRate;
    } else {
      throw EXCHANGE_RATE_NOT_FOUND_EXCEPTION;
    }
  }

  private ExchangeRate cacheExchangeRate(ExchangeRateDto exchangeRateDto) {
    ExchangeRate exchangeRate = fromExchangeRateDto(
        exchangeRateDto,
        getExpirationInSeconds(exchangeRateDto)
    );

    return exchangeRateRepository.save(exchangeRate);
  }

  public long getExpirationInSeconds(ExchangeRateDto exchangeRateDto) {
    long expirationInSeconds = getSecondsBetween(
        ZonedDateTime.now(UTC_ZONE_ID),
        toUtcDateTime(exchangeRateDto.getRecordDate().plusMonths(validMonths))
    );

    return expirationInSeconds >= 0 ? expirationInSeconds : 0;
  }
}
