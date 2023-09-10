package per.duyd.interview.pts.service;

import static per.duyd.interview.pts.mapper.ExchangeRateMapper.fromExchangeRateDto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
    return exchangeRateRepository.findByCurrencyAndEffectiveDateBetween(currency,
            transactionDate.minusMonths(validMonths), transactionDate)
        .stream().findFirst()
        .orElseGet(() -> getAndCacheExchangeRateFromApi(currency, transactionDate));
  }

  private ExchangeRate getAndCacheExchangeRateFromApi(String currency, LocalDate transactionDate) {
    ExchangeRateDto exchangeRateDto =
        treasuryClient.getLatestExchangeRate(currency, transactionDate);
    return cacheExchangeRate(exchangeRateDto);
  }

  private ExchangeRate cacheExchangeRate(ExchangeRateDto exchangeRateDto) {
    ExchangeRate exchangeRate = fromExchangeRateDto(exchangeRateDto);

    return exchangeRateRepository.save(exchangeRate);
  }
}
