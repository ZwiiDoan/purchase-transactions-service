package per.duyd.interview.pts.client;

import static per.duyd.interview.pts.exception.DataNotFoundException.EXCHANGE_RATE_NOT_FOUND_EXCEPTION;
import static per.duyd.interview.pts.util.DateTimeUtil.UTC_ZONE_ID;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import per.duyd.interview.pts.advice.TrackDownstreamInvocation;
import per.duyd.interview.pts.dto.ExchangeRateDto;
import per.duyd.interview.pts.dto.ExchangeRateResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class TreasuryClient {

  public static final String TREASURY_EXCHANGE_RATE_API_NAME = "TreasuryExchangeRate";
  private static final String LATEST_EXCHANGE_RATE_QUERY_TEMPLATE =
      "?fields=record_date,exchange_rate,country_currency_desc"
          + "&filter=country_currency_desc:eq:%s,record_date:gte:%s"
          + "&sort=-record_date"
          + "&page[number]=1&page[size]=1";

  private final RestTemplate treasuryRestTemplate;

  @Value("${services.treasury.baseUrl}")
  private String baseUrl = "http://localhost:8090";

  @Value("${services.treasury.endpoint}")
  private String exchangeRateEndpoint = "/v1/accounting/od/rates_of_exchange";

  @Value("${application.exchangeRate.validMonths}")
  private int validMonths = 6;

  @CircuitBreaker(name = TREASURY_EXCHANGE_RATE_API_NAME)
  @RateLimiter(name = TREASURY_EXCHANGE_RATE_API_NAME)
  @Bulkhead(name = TREASURY_EXCHANGE_RATE_API_NAME)
  @Retry(name = TREASURY_EXCHANGE_RATE_API_NAME)
  @TrackDownstreamInvocation(downstreamName = TREASURY_EXCHANGE_RATE_API_NAME, endpoint = "/v1/accounting/od/rates_of_exchange")
  public @NotNull ExchangeRateDto getLatestExchangeRate(@NotNull String currency) {
    ResponseEntity<ExchangeRateResponse> responseEntity = treasuryRestTemplate.getForEntity(
        baseUrl + exchangeRateEndpoint + getLatestExchangeRateQuery(currency),
        ExchangeRateResponse.class
    );

    return Optional.ofNullable(responseEntity.getBody())
        .map(ExchangeRateResponse::getExchangeRateDtos)
        .flatMap(exchangeRates -> exchangeRates.stream().findFirst())
        .orElseThrow(() -> EXCHANGE_RATE_NOT_FOUND_EXCEPTION);
  }

  private String getLatestExchangeRateQuery(@NotNull String currency) {
    return String.format(LATEST_EXCHANGE_RATE_QUERY_TEMPLATE, currency,
        LocalDate.now(UTC_ZONE_ID).minusMonths(validMonths)
            .format(DateTimeFormatter.ISO_LOCAL_DATE));
  }
}
